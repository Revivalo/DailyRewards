package dev.revivalo.dailyrewards.managers.reward;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.cooldown.Cooldown;
import dev.revivalo.dailyrewards.managers.cooldown.CooldownManager;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.PlayerUtils;
import dev.revivalo.dailyrewards.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class RewardManager {

    public void autoClaim(final Player player, Set<RewardType> rewardTypes) {
        if (Config.CHECK_FOR_FULL_INVENTORY.asBoolean() && player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Lang.FULL_INVENTORY_MESSAGE.asColoredString());
            return;
        }
        final String formattedRewards = rewardTypes.stream()
                .map(this::getRewardsPlaceholder)
                .collect(Collectors.joining(", "));

        rewardTypes.forEach(rewardType -> this.claim(player, rewardType, false, false));
        TextUtils.sendListToPlayer(player, Lang.AUTO_CLAIMED_NOTIFICATION
                .asReplacedList(new HashMap<String, String>() {{
                    put("%rewards%", String.format(formattedRewards));
                }}));
    }

    @SuppressWarnings("deprecation")
    public void claim(final Player player, RewardType type, boolean fromCommand, boolean announce) {
        if (!type.isEnabled()) {
            player.sendMessage(Lang.DISABLED_REWARD.asColoredString());
            return;
        }
        if (!player.hasPermission("dailyreward." + type)) {
            if (!fromCommand) return;
            player.sendMessage(Lang.PERMISSION_MESSAGE.asColoredString());
            return;
        }
        if (Config.CHECK_FOR_FULL_INVENTORY.asBoolean() && player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Lang.FULL_INVENTORY_MESSAGE.asColoredString());
            return;
        }


        final Cooldown cooldown = UserHandler.getUser(player.getUniqueId()).get().getCooldownOfReward(type);
        if (cooldown.isClaimable()) {
            final String typeName = type.toString().toUpperCase();
            final Collection<String> rewardCommands = Config.valueOf(String.format("%s%s_REWARDS", typeName, DailyRewardsPlugin.isPremium(player, type)))
                    .asReplacedList(new HashMap<String, String>() {{
                        put("%player%", player.getName());
                    }});

            if (rewardCommands.size() == 0) {
                player.sendMessage(Lang.REWARDS_IS_NOT_SET.asColoredString());
            } else {
                rewardCommands.forEach(command -> Bukkit.dispatchCommand(DailyRewardsPlugin.getConsole(), command));
            }

            CooldownManager.setCooldown(player, type);
            UserHandler.getUser(player.getUniqueId()).get().updateCooldowns(new HashMap<RewardType, Long>() {{
                put(type, System.currentTimeMillis() + type.getCooldown());
            }});
            if (announce) {
                PlayerUtils.playSound(player, Config.valueOf(String.format("%s_SOUND", typeName)).asUppercase());

                player.sendTitle(
                        Lang.valueOf(String.format("%s_TITLE", typeName)).asColoredString(),
                        Lang.valueOf(String.format("%s_SUBTITLE", typeName)).asColoredString());

                if (Config.ANNOUNCE_ENABLED.asBoolean()) {
                    Bukkit.broadcastMessage(Lang.valueOf(String.format("%s%s_COLLECTED", typeName, DailyRewardsPlugin.isPremium(player, type)))
                            .asPlaceholderReplacedText(player)
                            .replace("%player%", player.getName()));
                }
            }
            if (!fromCommand) player.closeInventory();

        } else {
            if (fromCommand) {
                player.sendMessage(Lang.COOLDOWN_MESSAGE.asColoredString()
                        .replace("%type%", getRewardsPlaceholder(type))
                        .replace("%time%", cooldown.getFormat(type.getCooldownFormat())));
                return;
            }
            PlayerUtils.playSound(player, Config.UNAVAILABLE_REWARD_SOUND.asUppercase());
        }
        //});
    }

    public String resetPlayer(final OfflinePlayer player, String typeString) {
        final boolean isPlayerOnline = player.isOnline();

        if (!isPlayerOnline && !player.hasPlayedBefore())
            return Lang.UNAVAILABLE_PLAYER.asColoredString().replace("%player%", player.getName());

        HashMap<RewardType, Long> changes;

        if (typeString.equalsIgnoreCase("all")) {
            changes = new HashMap<RewardType, Long>() {{
                put(RewardType.DAILY, 0L);
                put(RewardType.WEEKLY, 0L);
                put(RewardType.MONTHLY, 0L);
            }};

        } else {
            final RewardType type = RewardType.findByName(typeString);
            try {
                changes = new HashMap<RewardType, Long>() {{
                    put(type, 0L);
                }};

                //if (player.isOnline())
            } catch (IllegalArgumentException ex) {
                return Lang.INCOMPLETE_REWARD_RESET.asColoredString();
            }
        }

        DataManager.setValues(
                player.getUniqueId(),
                changes
        );

        //Bukkit.getLogger().info(DataManager.getAvailableRewards(player.getPlayer()).size() + "");
        //if (isPlayerOnline) UserHandler.getUser(player.getUniqueId()).get().setAvailableRewards((short) DataManager.getAvailableRewards(player.getPlayer()).size());

        return Lang.REWARD_RESET.asColoredString().replace("%type%", typeString).replace("%player%", player.getName());
    }

    private String getRewardsPlaceholder(final RewardType reward) {
        switch (reward) {
            case DAILY:
                return Config.DAILY_PLACEHOLDER.asString();
            case WEEKLY:
                return Config.WEEKLY_PLACEHOLDER.asString();
        }
        return Config.MONTHLY_PLACEHOLDER.asString();
    }
}
