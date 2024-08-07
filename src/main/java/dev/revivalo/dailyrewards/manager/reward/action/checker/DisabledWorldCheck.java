package dev.revivalo.dailyrewards.manager.reward.action.checker;

import dev.revivalo.dailyrewards.configuration.file.Lang;
import dev.revivalo.dailyrewards.manager.reward.action.response.ActionResponse;
import dev.revivalo.dailyrewards.manager.reward.action.response.ClaimActionResponse;
import dev.revivalo.dailyrewards.util.PermissionUtil;
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
    public PermissionUtil.Permission getBypassPermission() {
        return PermissionUtil.Permission.DISABLED_WORLDS_BYPASS;
    }
}
