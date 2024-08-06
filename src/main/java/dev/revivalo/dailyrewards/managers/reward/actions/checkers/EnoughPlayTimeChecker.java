package dev.revivalo.dailyrewards.managers.reward.actions.checkers;

import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.actions.responses.ActionResponse;
import dev.revivalo.dailyrewards.managers.reward.actions.responses.ClaimActionResponse;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import dev.revivalo.dailyrewards.utils.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class EnoughPlayTimeChecker implements Checker {
    private String failedCheckMessage;

    @Override
    public boolean check(Player player) {
        final int requiredPlayTimeInMinutes = Config.FIRST_TIME_JOIN_REQUIRED_PLAY_TIME.asInt();
        if (requiredPlayTimeInMinutes != 0) {
            final float actualPlayTimeInMinutes = PlayerUtils.getPlayersPlayTimeInMinutes(player);
            if (actualPlayTimeInMinutes < requiredPlayTimeInMinutes) {
                failedCheckMessage =
                        Lang.NOT_ENOUGH_REQUIRED_TIME_TO_CLAIM.asReplacedString(
                                player, new HashMap<String, String>() {{
                                    put("%requiredMinutes%", String.valueOf(requiredPlayTimeInMinutes));
                                    put("%minutes%", String.valueOf(Math.round(requiredPlayTimeInMinutes - actualPlayTimeInMinutes)));
                                }}
                        );
                return false;
            }
        }

        return true;
    }

    @Override
    public String getFailedCheckMessage() {
        return failedCheckMessage;
    }

    @Override
    public ActionResponse getClaimActionResponse() {
        return ClaimActionResponse.INSUFFICIENT_PLAY_TIME;
    }

    @Override
    public PermissionUtils.Permission getBypassPermission() {
        return PermissionUtils.Permission.REQUIRED_PLAYTIME_BYPASS;
    }
}