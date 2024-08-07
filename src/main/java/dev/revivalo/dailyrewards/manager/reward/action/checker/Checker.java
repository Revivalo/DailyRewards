package dev.revivalo.dailyrewards.manager.reward.action.checker;

import dev.revivalo.dailyrewards.manager.reward.action.response.ActionResponse;
import dev.revivalo.dailyrewards.util.PermissionUtil;
import org.bukkit.entity.Player;

public interface Checker {
    boolean check(Player player);

    String getFailedCheckMessage();

    ActionResponse getClaimActionResponse();

    PermissionUtil.Permission getBypassPermission();
}