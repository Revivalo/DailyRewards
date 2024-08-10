package dev.revivalo.dailyrewards.manager.reward.action;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.file.Config;
import dev.revivalo.dailyrewards.configuration.file.Lang;
import dev.revivalo.dailyrewards.manager.reward.action.checker.Checker;
import dev.revivalo.dailyrewards.manager.reward.action.response.ActionResponse;
import dev.revivalo.dailyrewards.util.PermissionUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public interface RewardAction<T> {
    default ActionResponse preCheck(OfflinePlayer player, T extra) {
        if (player == null) {
            getExecutor().sendMessage(Lang.UNAVAILABLE_PLAYER.asColoredString().replace("%player%", "null"));
            return ActionResponse.Type.UNAVAILABLE_PLAYER;
        }
        if (!PermissionUtil.hasPermission(getExecutor(), getPermission())) {
            getExecutor().sendMessage(Lang.PERMISSION_MSG.asColoredString((Player) getExecutor()));
            return ActionResponse.Type.NO_PERMISSION;
        }

        ActionResponse response = execute(player, extra);

        if (menuShouldOpen() && Config.OPEN_MENU_AFTER_CLAIMING.asBoolean()) {
            DailyRewardsPlugin.getMenuManager().openRewardsMenu(player.getPlayer());
        }

        return response;
    }

    ActionResponse execute(OfflinePlayer player, T extra);

    boolean menuShouldOpen();

    CommandSender getExecutor();

    PermissionUtil.Permission getPermission();

    List<Checker> getCheckers();
}