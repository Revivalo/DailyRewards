package dev.revivalo.dailyrewards.managers.reward.actions.checkers;

import dev.revivalo.dailyrewards.managers.reward.actions.responses.ActionResponse;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import org.bukkit.entity.Player;

public interface Checker {
    boolean check(Player player);

    String getFailedCheckMessage();

    ActionResponse getClaimActionResponse();

    PermissionUtils.Permission getBypassPermission();
}