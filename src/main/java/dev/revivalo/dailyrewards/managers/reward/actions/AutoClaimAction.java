package dev.revivalo.dailyrewards.managers.reward.actions;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import dev.revivalo.dailyrewards.utils.PlayerUtils;
import dev.revivalo.dailyrewards.utils.TextUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class AutoClaimAction implements RewardAction<Set<RewardType>> {

    @Override
    public void execute(OfflinePlayer offlinePlayer, Set<RewardType> rewardTypes, boolean fromCommand) {
        final User user = UserHandler.getUser(offlinePlayer.getUniqueId());
        if (!user.isOnline()) {
            return;
        }

        final Player player = offlinePlayer.getPlayer();
        if (!PlayerUtils.doesPlayerHaveEnoughPlayTime(player)) {
            return;
        }

        if (Config.CHECK_FOR_FULL_INVENTORY.asBoolean() && player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Lang.FULL_INVENTORY_MESSAGE.asColoredString());
            return;
        }

        final String formattedRewards = rewardTypes.stream()
                .map(DailyRewardsPlugin.getRewardManager()::getRewardsPlaceholder)
                .collect(Collectors.joining(", "));

        rewardTypes.forEach(
                rewardType ->
                        new ClaimAction(player)
                                .disableAnnounce()
                                .preCheck(player, rewardType, false)
        );

        TextUtils.sendListToPlayer(player, Lang.AUTO_CLAIMED_NOTIFICATION
                .asReplacedList(new HashMap<String, String>() {{
                    put("%rewards%", String.format(formattedRewards));
                }}));
    }

    @Override
    public CommandSender getExecutor() {
        return null;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return null;
    }
}
