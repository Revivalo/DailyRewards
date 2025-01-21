package eu.athelion.dailyrewards.manager.reward.action.checker;

import eu.athelion.dailyrewards.configuration.file.Lang;
import eu.athelion.dailyrewards.manager.reward.action.response.ActionResponse;
import eu.athelion.dailyrewards.manager.reward.action.response.ClaimActionResponse;
import eu.athelion.dailyrewards.util.PermissionUtil;
import org.bukkit.entity.Player;

public class AvailableSlotsInInventoryChecker implements Checker {

    private String failedCheckMessage;

    @Override
    public boolean check(Player player) {
        if (player.getInventory().firstEmpty() == -1) {
            failedCheckMessage = Lang.FULL_INVENTORY_MESSAGE.asColoredString(player);
            return false;
        }

        return true;
    }

    @Override
    public String getFailedCheckMessage() {
        return failedCheckMessage;
    }

    @Override
    public ActionResponse getClaimActionResponse() {
        return ClaimActionResponse.NOT_ENOUGH_REQUIRED_INVENTORY_SLOTS;
    }

    @Override
    public PermissionUtil.Permission getBypassPermission() {
        return PermissionUtil.Permission.REQUIRED_FREE_SLOTS_BYPASS;
    }
}