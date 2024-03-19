package dev.revivalo.dailyrewards.managers.reward.actions.checkers;

import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.actions.responses.ActionResponse;
import dev.revivalo.dailyrewards.managers.reward.actions.responses.ClaimActionResponse;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import org.bukkit.entity.Player;

public class AvailableSlotsInInventoryChecker implements Checker {

    private String failedCheckMessage;

    @Override
    public boolean check(Player player) {
        if (player.getInventory().firstEmpty() == -1) {
            failedCheckMessage = Lang.FULL_INVENTORY_MESSAGE.asColoredString();
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
    public PermissionUtils.Permission getBypassPermission() {
        return PermissionUtils.Permission.REQUIRED_PLAYTIME_BYPASS;
    }
}