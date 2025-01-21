package eu.athelion.dailyrewards.manager.reward.action.checker;

import eu.athelion.dailyrewards.configuration.file.Config;
import eu.athelion.dailyrewards.configuration.file.Lang;
import eu.athelion.dailyrewards.manager.reward.action.response.ActionResponse;
import eu.athelion.dailyrewards.manager.reward.action.response.ClaimActionResponse;
import eu.athelion.dailyrewards.util.PermissionUtil;
import eu.athelion.dailyrewards.util.PlayerUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class EnoughPlayTimeChecker implements Checker {
    private String failedCheckMessage;

    @Override
    public boolean check(Player player) {
        final int requiredPlayTimeInMinutes = Config.FIRST_TIME_JOIN_REQUIRED_PLAY_TIME.asInt();
        if (requiredPlayTimeInMinutes != 0) {
            final float actualPlayTimeInMinutes = PlayerUtil.getPlayersPlayTimeInMinutes(player);
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
    public PermissionUtil.Permission getBypassPermission() {
        return PermissionUtil.Permission.REQUIRED_PLAYTIME_BYPASS;
    }
}