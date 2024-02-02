package dev.revivalo.dailyrewards.managers.reward.actions;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
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
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Set;

public class AutoClaimAction implements RewardAction<Set<RewardType>> {

    @Override
    public void execute(OfflinePlayer offlinePlayer, Set<RewardType> rewardTypes, boolean fromCommand) {
        final User user = UserHandler.getUser(offlinePlayer.getUniqueId());
        if (!user.isOnline()) {
            return;
        }

        final Player player = offlinePlayer.getPlayer();
        if (!PlayerUtils.doesPlayerHaveEnoughPlayTime(player)) {
            return;
        }

        StringBuilder formattedRewards = new StringBuilder();

        BukkitScheduler scheduler = Bukkit.getScheduler();
        int delay = 2;

        for (RewardType rewardType : rewardTypes) {
            scheduler.runTaskLater(DailyRewardsPlugin.get(), () -> {
                if (player.hasPermission(rewardType.getPermission())) {
                    if (Config.CHECK_FOR_FULL_INVENTORY.asBoolean() && player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(Lang.FULL_INVENTORY_MESSAGE_AUTO_CLAIM.asColoredString().replace("%reward%", rewardType.toString()));
                        return;
                    }

                    formattedRewards.append(DailyRewardsPlugin.getRewardManager().getRewardsPlaceholder(rewardType)).append(", ");

                    new ClaimAction(player)
                            .disableAnnounce()
                            .preCheck(player, rewardType, false);
                }
            }, delay);

            delay += 2;
        }

        scheduler.runTaskLater(DailyRewardsPlugin.get(), () -> {
            if (formattedRewards.length() > 0) {
                formattedRewards.setLength(formattedRewards.length() - 2); // Odstranění posledních dvou znaků (", ")

                TextUtils.sendListToPlayer(player, Lang.AUTO_CLAIMED_NOTIFICATION
                        .asReplacedList(new HashMap<String, String>() {{
                            put("%rewards%", formattedRewards.toString());
                        }}));
            }
        }, delay);
    }

    @Override
    public CommandSender getExecutor() {
        return null;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return null;
    }
}
