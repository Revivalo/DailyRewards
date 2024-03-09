package dev.revivalo.dailyrewards.managers.reward.actions;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.api.events.PlayerClaimRewardEvent;
import dev.revivalo.dailyrewards.api.events.PlayerPreClaimRewardEvent;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.cooldown.CooldownManager;
import dev.revivalo.dailyrewards.managers.reward.ActionsExecutor;
import dev.revivalo.dailyrewards.managers.reward.Reward;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import dev.revivalo.dailyrewards.utils.PlayerUtils;
import dev.revivalo.dailyrewards.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class ClaimAction implements RewardAction<RewardType> {
    private final CommandSender executor;
    private boolean announce = true;
    public ClaimAction(CommandSender executor) {
        this.executor = executor;
    }

    @Override
    public void execute(OfflinePlayer offlinePlayer, RewardType type, boolean fromCommand) {
        final User user = UserHandler.getUser(offlinePlayer.getUniqueId());
        if (!user.isOnline()) {
            return;
        }

        final Player player = offlinePlayer.getPlayer();
        final Reward reward = DailyRewardsPlugin.getRewardManager()
                .getRewardByType(type)
                .orElse(null);

        if (reward == null) {
            player.sendMessage(Lang.DISABLED_REWARD.asColoredString());
            return;
        }

        final List<String> rewardActions = TextUtils.replaceList(DailyRewardsPlugin.isPremium(player, type) ? reward.getPremiumRewards() : reward.getDefaultRewards(), new HashMap<String, String>() {{
            put("player", player.getName());
        }});

        PlayerPreClaimRewardEvent playerPreClaimRewardEvent = new PlayerPreClaimRewardEvent(player, reward.getRewardType(), rewardActions);
        Bukkit.getPluginManager().callEvent(playerPreClaimRewardEvent);

        if (playerPreClaimRewardEvent.isCancelled()) {
            return;
        }

        if (!player.hasPermission(type.getPermission())) {
            //if (!fromCommand) return;
            player.sendMessage(Lang.INSUFFICIENT_PERMISSION_MESSAGE.asColoredString());
            return;
        }

        if (!PermissionUtils.hasPermission(player, PermissionUtils.Permission.REQUIRED_PLAYTIME_BYPASS)) {
            if (!PlayerUtils.doesPlayerHaveEnoughPlayTime(player))
                return;
        }

        if (PlayerUtils.isPlayerInDisabledWorld(player, true))
            return;

        if (Config.CHECK_FOR_FULL_INVENTORY.asBoolean() && player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Lang.FULL_INVENTORY_MESSAGE.asColoredString());
            return;
        }

        user.getCooldownOfReward(type).thenAccept(cooldown -> {
            if (cooldown.isClaimable()) {

                if (rewardActions.isEmpty()) {
                    player.sendMessage(Lang.REWARDS_ARE_NOT_SET.asColoredString());
                } else {
                    PlayerClaimRewardEvent playerClaimRewardEvent = new PlayerClaimRewardEvent(player, reward.getRewardType());
                    Bukkit.getPluginManager().callEvent(playerClaimRewardEvent);

                    ActionsExecutor.executeActions(
                            player,
                            reward.getRewardName(),
                            TextUtils.findAndReturnActions(rewardActions)
                    );

                    //rewardCommands.forEach(command -> Bukkit.dispatchCommand(DailyRewardsPlugin.getConsole(), command));
                }

                CooldownManager.setCooldown(user, reward);

                if (announce) {
                    PlayerUtils.playSound(player, reward.getSound());

                    player.sendTitle(reward.getTitle(), reward.getSubtitle());

                    if (Config.ANNOUNCE_ENABLED.asBoolean()) {
                        Bukkit.broadcastMessage((DailyRewardsPlugin.isPremium(player, type) ? reward.getCollectedPremiumMessage() : reward.getCollectedMessage()).replace("%player%", player.getName()));
                    }
                }
                if (!fromCommand) player.closeInventory();

            } else {
                if (fromCommand) {
                    player.sendMessage(Lang.COOLDOWN_MESSAGE.asReplacedString(new HashMap<String, String>() {{
                        put("%type%", DailyRewardsPlugin.getRewardManager().getRewardsPlaceholder(type));
                        put("%time%", cooldown.getFormat(reward.getCooldownFormat()));
                    }}));
                    return;
                }
                PlayerUtils.playSound(player, Config.UNAVAILABLE_REWARD_SOUND.asUppercase());
            }
        });
    }

    public ClaimAction disableAnnounce() {
        this.announce = false;
        return this;
    }

    @Override
    public CommandSender getExecutor() {
        return executor;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return null;
    }
}
