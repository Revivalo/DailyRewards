package dev.revivalo.dailyrewards.managers.reward.actions;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.actions.checkers.Checker;
import dev.revivalo.dailyrewards.managers.reward.actions.responses.ActionResponse;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface RewardAction<T> {
    default ActionResponse preCheck(OfflinePlayer player, T extra, boolean fromCommand) {
        if (!PermissionUtils.hasPermission(getExecutor(), getPermission())) {
            getExecutor().sendMessage(Lang.INSUFFICIENT_PERMISSION_MESSAGE.asColoredString());
            return ActionResponse.NO_PERMISSION;
        }

        ActionResponse response = execute(player, extra, fromCommand);

        if (!fromCommand) {
            DailyRewardsPlugin.getMenuManager().openRewardsMenu(player.getPlayer());
        }

        return response;
    }

    ActionResponse execute(OfflinePlayer player, T extra, boolean fromCommand);

    CommandSender getExecutor();

    PermissionUtils.Permission getPermission();

    List<Checker> getCheckers();
}
