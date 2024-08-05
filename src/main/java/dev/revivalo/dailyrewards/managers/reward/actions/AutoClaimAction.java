package dev.revivalo.dailyrewards.managers.reward.actions;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.managers.reward.actions.checkers.Checker;
import dev.revivalo.dailyrewards.managers.reward.actions.responses.ActionResponse;
import dev.revivalo.dailyrewards.managers.reward.actions.responses.ClaimActionResponse;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import dev.revivalo.dailyrewards.utils.TextUtils;
import dev.revivalo.dailyrewards.utils.VersionUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public class AutoClaimAction implements RewardAction<Set<RewardType>> {

    @Override
    public ActionResponse execute(OfflinePlayer offlinePlayer, Set<RewardType> rewardTypes) {
        final User user = UserHandler.getUser(offlinePlayer.getUniqueId());
        if (user == null) {
            return ActionResponse.Type.UNAVAILABLE_PLAYER;
        }

        final Player player = user.getPlayer();

        BukkitScheduler scheduler = Bukkit.getScheduler();

        scheduler.runTaskLater(DailyRewardsPlugin.get(), () -> {
            Set<RewardType> claimedRewards = new HashSet<>();
            Map<RewardType, ActionResponse> notClaimedRewards = new HashMap<>();
            for (RewardType rewardType : rewardTypes) {

                if (player.hasPermission(rewardType.getPermission())) {

                    ActionResponse response = new ClaimAction(player)
                            .disableAnnounce()
                            .disableMenuOpening()
                            .preCheck(player, rewardType);

                    if (ActionResponse.isProceeded(response)) {
                        claimedRewards.add(rewardType);
                    } else {
                        notClaimedRewards.put(rewardType, response);
                    }

                }
            }

            if (!notClaimedRewards.isEmpty()) {
                BaseComponent[] msg = TextComponent.fromLegacyText(Lang.AUTO_CLAIM_FAILED.asColoredString(player));
                StringBuilder notClaimRewardsBuffer = new StringBuilder();

                if (!VersionUtils.isLegacyVersion()) {
                    String format = Lang.AUTO_CLAIM_FAILED_HOVER_TEXT_LIST_FORMAT.asColoredString(player);
                    for (Map.Entry<RewardType, ActionResponse> notClaimedReward : notClaimedRewards.entrySet()) {
                        notClaimRewardsBuffer
                                .append(" \n")
                                .append(format
                                        .replace("%reward%", notClaimedReward.getKey().getName())
                                        .replace("%reason%", ((ClaimActionResponse) notClaimedReward.getValue()).getMessage())
                                );
                    }
                    for (BaseComponent bc : msg) {
                        bc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                                Lang.AUTO_CLAIM_FAILED_HOVER_TEXT.asColoredString(player)
                                        .replace("%format%",
                                                notClaimRewardsBuffer.toString())))
                        );
                    }
                }

                player.spigot().sendMessage(msg);
                //player.sendMessage(Lang.AUTO_CLAIM_FAILED.asColoredString());
            }

            if (!claimedRewards.isEmpty()) {
                StringBuilder claimedRewardsBuilder = new StringBuilder();
                for (RewardType availableReward : claimedRewards) {
                    claimedRewardsBuilder.append("\n ").append(availableReward.getName());
                }
                TextUtils.sendListToPlayer(player, Lang.JOIN_AUTO_CLAIM_NOTIFICATION
                        .asReplacedList(new HashMap<String, String>() {{
                                            put("%listOfRewards%", claimedRewardsBuilder.toString());
                                        }}
                        )
                );
            }
        }, Config.JOIN_AUTO_CLAIM_DELAY.asInt() * 20L);

        return ActionResponse.Type.PROCEEDED;
    }

    @Override
    public boolean menuShouldOpen() {
        return false;
    }

    @Override
    public CommandSender getExecutor() {
        return null;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.AUTO_CLAIM_SETTING;
    }

    @Override
    public List<Checker> getCheckers() {
        return Collections.emptyList();
    }
}
