package eu.athelion.dailyrewards.manager.reward.action.checker;

import eu.athelion.dailyrewards.manager.reward.action.response.ActionResponse;
import eu.athelion.dailyrewards.util.PermissionUtil;
import org.bukkit.entity.Player;

public interface Checker {
    boolean check(Player player);

    String getFailedCheckMessage();

    ActionResponse getClaimActionResponse();

    PermissionUtil.Permission getBypassPermission();
}