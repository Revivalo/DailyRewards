package eu.athelion.dailyrewards.manager.reward.action;

import eu.athelion.dailyrewards.data.DataManager;
import eu.athelion.dailyrewards.configuration.file.Lang;
import eu.athelion.dailyrewards.manager.reward.RewardType;
import eu.athelion.dailyrewards.manager.reward.action.checker.Checker;
import eu.athelion.dailyrewards.manager.reward.action.response.ActionResponse;
import eu.athelion.dailyrewards.manager.reward.action.response.ResetActionResponse;
import eu.athelion.dailyrewards.user.UserHandler;
import eu.athelion.dailyrewards.util.PermissionUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ResetAction implements RewardAction<String> {
    private final CommandSender executor;

    public ResetAction(CommandSender executor) {
        this.executor = executor;
    }

    @Override
    public ActionResponse execute(OfflinePlayer offlinePlayer, String typeString) {
        final boolean isPlayerOnline = offlinePlayer.isOnline();

        String nickname = offlinePlayer.getName() == null ? "Unknown" : offlinePlayer.getName();

        if (!isPlayerOnline && !offlinePlayer.hasPlayedBefore()) {
            executor.sendMessage(Lang.UNAVAILABLE_PLAYER.asColoredString().replace("%player%", nickname));
            return ActionResponse.Type.UNAVAILABLE_PLAYER;
        }

        HashMap<String, Object> changes;

        if (typeString.equalsIgnoreCase("all")) {
            changes = new HashMap<String, Object>() {{
                put(RewardType.DAILY.toString(), "0");
                put(RewardType.WEEKLY.toString(), "0");
                put(RewardType.MONTHLY.toString(), "0");
            }};

        } else {
            final RewardType type = RewardType.findByName(typeString);
            try {
                changes = new HashMap<String, Object>() {{
                    put(type.toString(), 0L);
                }};

            } catch (IllegalArgumentException ex) {
                executor.sendMessage(Lang.INCOMPLETE_REWARD_RESET.asColoredString());
                return ResetActionResponse.INCOMPLETE_REWARD_RESET;
            }
        }

        boolean updated = DataManager.updateValues(
                offlinePlayer.getUniqueId(),
                UserHandler.getUser(offlinePlayer.getPlayer()),
                changes
        );

        if (updated) {
            executor.sendMessage(Lang.REWARD_RESET.asColoredString().replace("%type%", typeString).replace("%player%", nickname));
        } else {
            executor.sendMessage(Lang.UNAVAILABLE_PLAYER.asColoredString().replace("%player%", nickname));
        }

        return ActionResponse.Type.PROCEEDED;
    }

    @Override
    public boolean menuShouldOpen() {
        return false;
    }

    @Override
    public CommandSender getExecutor() {
        return executor;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.RESET_FOR_OTHERS;
    }

    @Override
    public List<Checker> getCheckers() {
        return Collections.emptyList();
    }
}