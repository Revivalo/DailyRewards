package eu.athelion.dailyrewards.manager.reward.action;

import eu.athelion.dailyrewards.configuration.file.Lang;
import eu.athelion.dailyrewards.manager.reward.RewardType;
import eu.athelion.dailyrewards.manager.reward.action.checker.Checker;
import eu.athelion.dailyrewards.manager.reward.action.response.ActionResponse;
import eu.athelion.dailyrewards.manager.reward.action.response.ClaimActionResponse;
import eu.athelion.dailyrewards.user.User;
import eu.athelion.dailyrewards.user.UserHandler;
import eu.athelion.dailyrewards.util.PermissionUtil;
import eu.athelion.dailyrewards.util.TextUtil;
import eu.athelion.dailyrewards.util.VersionUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class AutoClaimAction implements RewardAction<Set<RewardType>> {

    @Override
    public ActionResponse execute(OfflinePlayer offlinePlayer, Set<RewardType> rewardTypes) {
        final User user = UserHandler.getUser(offlinePlayer.getUniqueId());
        if (user == null) {
            return ActionResponse.Type.UNAVAILABLE_PLAYER;
        }

        final Player player = user.getPlayer();

        Set<RewardType> claimedRewards = new HashSet<>();
        Map<RewardType, ActionResponse> notClaimedRewards = new HashMap<>();
        for (RewardType rewardType : rewardTypes) {

            if (player.hasPermission(rewardType.getPermission())) {

                ActionResponse response = new ClaimAction(player)
                        .disableAnnounce()
                        .disableMenuOpening()
                        .preCheck(player, rewardType);

                if (ActionResponse.isProceeded(response)) {
                    claimedRewards.add(rewardType);
                } else {
                    notClaimedRewards.put(rewardType, response);
                }

            }
        }

        if (!notClaimedRewards.isEmpty()) {
            BaseComponent[] msg = TextComponent.fromLegacyText(Lang.AUTO_CLAIM_FAILED.asColoredString(player));
            StringBuilder notClaimRewardsBuffer = new StringBuilder();

            if (!VersionUtil.isLegacyVersion()) {
                String format = Lang.AUTO_CLAIM_FAILED_HOVER_TEXT_LIST_FORMAT.asColoredString(player);
                for (Map.Entry<RewardType, ActionResponse> notClaimedReward : notClaimedRewards.entrySet()) {
                    notClaimRewardsBuffer
                            .append(" \n")
                            .append(format
                                    .replace("%reward%", notClaimedReward.getKey().getName())
                                    .replace("%reason%", ((ClaimActionResponse) notClaimedReward.getValue()).getMessage())
                            );
                }
                for (BaseComponent bc : msg) {
                    bc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                            Lang.AUTO_CLAIM_FAILED_HOVER_TEXT.asColoredString(player)
                                    .replace("%format%",
                                            notClaimRewardsBuffer.toString())))
                    );
                }
            }

            player.spigot().sendMessage(msg);
            //player.sendMessage(Lang.AUTO_CLAIM_FAILED.asColoredString());
        }

        if (!claimedRewards.isEmpty()) {
            StringBuilder claimedRewardsBuilder = new StringBuilder();
            for (RewardType availableReward : claimedRewards) {
                claimedRewardsBuilder.append("\n ").append(availableReward.getName());
            }
            TextUtil.sendListToPlayer(player, Lang.JOIN_AUTO_CLAIM_NOTIFICATION
                    .asReplacedList(new HashMap<String, String>() {{
                                        put("%listOfRewards%", claimedRewardsBuilder.toString());
                                    }}
                    )
            );
        }


        return ActionResponse.Type.PROCEEDED;
    }

    @Override
    public boolean menuShouldOpen() {
        return false;
    }

    @Override
    public CommandSender getExecutor() {
        return null;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.AUTO_CLAIM_SETTING;
    }

    @Override
    public List<Checker> getCheckers() {
        return Collections.emptyList();
    }
}
