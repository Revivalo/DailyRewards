package dev.revivalo.dailyrewards.managers.reward.actions.checkers;

import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.actions.responses.ActionResponse;
import dev.revivalo.dailyrewards.managers.reward.actions.responses.ClaimActionResponse;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class DisabledWorldCheck implements Checker {
    private String failedCheckMessage;
    private final List<String> disabledWorlds;

    public DisabledWorldCheck(List<String> disabledWorlds) {
        this.disabledWorlds = disabledWorlds;
    }

    @Override
    public boolean check(Player player) {
        final String playerWorldName = player.getWorld().getName();
        if (disabledWorlds.stream().anyMatch(worldName -> worldName.equalsIgnoreCase(playerWorldName))) {
            failedCheckMessage = Lang.CLAIMING_IN_DISABLED_WORLD.asColoredString(player).replace("%world%", playerWorldName);
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
        return ClaimActionResponse.LOCATED_IN_DISABLED_WORLD;
    }

    @Override
    public PermissionUtils.Permission getBypassPermission() {
        return PermissionUtils.Permission.DISABLED_WORLDS_BYPASS;
    }
}
