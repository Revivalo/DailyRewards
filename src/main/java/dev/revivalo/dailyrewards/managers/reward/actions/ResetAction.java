package dev.revivalo.dailyrewards.managers.reward.actions;

import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class ResetAction implements RewardAction<String> {
    private final CommandSender executor;

    public ResetAction(CommandSender executor) {
        this.executor = executor;
    }

    @Override
    public void execute(OfflinePlayer offlinePlayer, String typeString, boolean fromCommand) {
        final boolean isPlayerOnline = offlinePlayer.isOnline();

        if (!isPlayerOnline && !offlinePlayer.hasPlayedBefore()) {
            executor.sendMessage(Lang.UNAVAILABLE_PLAYER.asColoredString().replace("%player%", offlinePlayer.getName()));
            return;
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
                return;
            }
        }

        boolean updated = DataManager.updateValues(
                offlinePlayer.getUniqueId(),
                UserHandler.getUser(offlinePlayer.getPlayer()),
                changes
        );

        if (updated) {
            executor.sendMessage(Lang.REWARD_RESET.asColoredString().replace("%type%", typeString).replace("%player%", offlinePlayer.getName()));
        } else {
            executor.sendMessage(Lang.UNAVAILABLE_PLAYER.asColoredString().replace("%player%", offlinePlayer.getName()));
        }
    }

    @Override
    public CommandSender getExecutor() {
        return executor;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.RESET_FOR_OTHERS;
    }
}
