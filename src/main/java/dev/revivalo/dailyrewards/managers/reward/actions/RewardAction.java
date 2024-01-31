package dev.revivalo.dailyrewards.managers.reward.actions;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public interface RewardAction<T> {
    default void preCheck(OfflinePlayer player, T extra, boolean fromCommand) {
        DailyRewardsPlugin.get().runSync(() -> {
            if (!PermissionUtils.hasPermission(getExecutor(), getPermission())) {
                getExecutor().sendMessage(Lang.INSUFFICIENT_PERMISSION_MESSAGE.asColoredString());
                return;
            }

            execute(player, extra, fromCommand);

            if (!fromCommand) {
                DailyRewardsPlugin.getMenuManager().openRewardsMenu(player.getPlayer());
            }
        });
    }

    void execute(OfflinePlayer player, T extra, boolean fromCommand);

    CommandSender getExecutor();

    PermissionUtils.Permission getPermission();
}
