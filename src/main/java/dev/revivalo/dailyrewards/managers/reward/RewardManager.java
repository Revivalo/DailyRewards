package dev.revivalo.dailyrewards.managers.reward;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.api.events.AutoClaimEvent;
import dev.revivalo.dailyrewards.api.events.PlayerClaimRewardEvent;
import dev.revivalo.dailyrewards.api.events.PlayerPreClaimRewardEvent;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.cooldown.CooldownManager;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import dev.revivalo.dailyrewards.utils.PlayerUtils;
import dev.revivalo.dailyrewards.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class RewardManager {
    private final Set<Reward> rewards;

    public RewardManager() {
        this.rewards = new HashSet<>();
        loadRewards();
    }

    public boolean processAutoClaimForUser(User user) {
        if (!user.hasEnabledAutoClaim())
            return false;

        if (user.getAvailableRewards().isEmpty())
            return false;

        AutoClaimEvent autoClaimEvent = new AutoClaimEvent(user.getPlayer(), user.getAvailableRewards());
        Bukkit.getPluginManager().callEvent(autoClaimEvent);

        if (autoClaimEvent.isCancelled())
            return false;

        Bukkit.getScheduler().runTaskLater(DailyRewardsPlugin.get(), () ->
                DailyRewardsPlugin.getRewardManager().claimRewardsAutomatically(user.getPlayer(), user.getAvailableRewards()), 3);
        return false;

    }

    public void claimRewardsAutomatically(final Player player, Set<RewardType> rewardTypes) {
        if (!PlayerUtils.doesPlayerHaveEnoughPlayTime(player))
            return;

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
        final Reward reward = getRewardByType(type).isPresent() ? getRewardByType(type).get() : null;

        if (reward == null) {
            player.sendMessage(Lang.DISABLED_REWARD.asColoredString());
            return;
        }

        final List<String> rewardCommands = TextUtils.replaceList(DailyRewardsPlugin.isPremium(player, type) ? reward.getPremiumRewards() : reward.getDefaultRewards(), new HashMap<String, String>() {{
            put("player", player.getName());
        }});

        PlayerPreClaimRewardEvent playerPreClaimRewardEvent = new PlayerPreClaimRewardEvent(player, reward.getRewardType(), rewardCommands);
        Bukkit.getPluginManager().callEvent(playerPreClaimRewardEvent);

        if (playerPreClaimRewardEvent.isCancelled())
            return;

        if (!player.hasPermission("dailyreward." + type)) {
            if (!fromCommand) return;
            player.sendMessage(Lang.PERMISSION_MESSAGE.asColoredString());
            return;
        }

        if (!PermissionUtils.hasPermission(player, PermissionUtils.Permission.REQUIRED_PLAYTIME_BYPASS)) {
            if (!PlayerUtils.doesPlayerHaveEnoughPlayTime(player))
                return;
        }

        if (PlayerUtils.isPlayerInDisabledWorld(player, true))
            return;

        if (Config.CHECK_FOR_FULL_INVENTORY.asBoolean() && player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Lang.FULL_INVENTORY_MESSAGE.asColoredString());
            return;
        }

        final User user = UserHandler.getUser(player.getUniqueId());

        user.getCooldownOfReward(type).thenAccept(cooldown -> {
            if (cooldown.isClaimable()) {

                if (rewardCommands.isEmpty()) {
                    player.sendMessage(Lang.REWARDS_ARE_NOT_SET.asColoredString());
                } else {
                    PlayerClaimRewardEvent playerClaimRewardEvent = new PlayerClaimRewardEvent(player, reward.getRewardType());
                    Bukkit.getPluginManager().callEvent(playerClaimRewardEvent);

                    rewardCommands.forEach(command -> Bukkit.dispatchCommand(DailyRewardsPlugin.getConsole(), command));
                }

                CooldownManager.setCooldown(user, reward);

                if (announce) {
                    PlayerUtils.playSound(player, reward.getSound());

                    player.sendTitle(reward.getTitle(), reward.getSubtitle());

                    if (Config.ANNOUNCE_ENABLED.asBoolean()) {
                        Bukkit.broadcastMessage((DailyRewardsPlugin.isPremium(player, type) ? reward.getCollectedPremiumMessage() : reward.getCollectedMessage()).replace("%player%", player.getName()));
                    }
                }
                if (!fromCommand) player.closeInventory();

            } else {
                if (fromCommand) {
                    player.sendMessage(Lang.COOLDOWN_MESSAGE.asReplacedString(new HashMap<String, String>() {{
                        put("%type%", getRewardsPlaceholder(type));
                        put("%time%", cooldown.getFormat(reward.getCooldownFormat()));
                    }}));
                    return;
                }
                PlayerUtils.playSound(player, Config.UNAVAILABLE_REWARD_SOUND.asUppercase());
            }
        });
    }

    public String resetPlayer(final OfflinePlayer player, String typeString) {
        final boolean isPlayerOnline = player.isOnline();

        if (!isPlayerOnline && !player.hasPlayedBefore())
            return Lang.UNAVAILABLE_PLAYER.asColoredString().replace("%player%", player.getName());

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
                return Lang.INCOMPLETE_REWARD_RESET.asColoredString();
            }
        }

        DataManager.updateValues(
                player.getUniqueId(),
                null,
                changes
        );

        return Lang.REWARD_RESET.asColoredString().replace("%type%", typeString).replace("%player%", player.getName());
    }

    public void loadRewards() {

        rewards.clear();
        if (Config.DAILY_ENABLED.asBoolean())
            rewards.add(new Reward(RewardType.DAILY, Config.DAILY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean(), Config.DAILY_COOLDOWN_FORMAT.asString(), Config.DAILY_COOLDOWN.asInt(), Config.DAILY_COOLDOWN_FORMAT.asString(), Config.DAILY_POSITION.asInt(), Config.DAILY_SOUND.asUppercase(), Lang.DAILY_TITLE.asColoredString(), Lang.DAILY_SUBTITLE.asColoredString(), Lang.DAILY_COLLECTED.asColoredString(), Lang.DAILY_PREMIUM_COLLECTED.asColoredString(), Config.DAILY_AVAILABLE_ITEM.asAnItem(), Config.DAILY_UNAVAILABLE_ITEM.asAnItem(), Lang.DAILY_DISPLAY_NAME_AVAILABLE.asColoredString(), Lang.DAILY_PREMIUM_DISPLAY_NAME_AVAILABLE.asColoredString(), Lang.DAILY_DISPLAY_NAME_UNAVAILABLE.asColoredString(), Lang.DAILY_PREMIUM_DISPLAY_NAME_UNAVAILABLE.asColoredString(), Lang.DAILY_AVAILABLE_LORE.asReplacedList(Collections.emptyMap()), Lang.DAILY_AVAILABLE_PREMIUM_LORE.asReplacedList(Collections.emptyMap()), Lang.DAILY_UNAVAILABLE_LORE.asReplacedList(Collections.emptyMap()), Lang.DAILY_PREMIUM_UNAVAILABLE_LORE.asReplacedList(Collections.emptyMap()), Config.DAILY_REWARDS.asReplacedList(Collections.emptyMap()), Config.DAILY_PREMIUM_REWARDS.asReplacedList(Collections.emptyMap())));
        if (Config.WEEKLY_ENABLED.asBoolean())
            rewards.add(new Reward(RewardType.WEEKLY, Config.WEEKLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean(), Config.WEEKLY_COOLDOWN_FORMAT.asString(), Config.WEEKLY_COOLDOWN.asInt(), Config.WEEKLY_COOLDOWN_FORMAT.asString(), Config.WEEKLY_POSITION.asInt(), Config.WEEKLY_SOUND.asUppercase(), Lang.WEEKLY_TITLE.asColoredString(), Lang.WEEKLY_SUBTITLE.asColoredString(), Lang.WEEKLY_COLLECTED.asColoredString(), Lang.WEEKLY_PREMIUM_COLLECTED.asColoredString(), Config.WEEKLY_AVAILABLE_ITEM.asAnItem(), Config.WEEKLY_UNAVAILABLE_ITEM.asAnItem(), Lang.WEEKLY_DISPLAY_NAME_AVAILABLE.asColoredString(), Lang.WEEKLY_PREMIUM_DISPLAY_NAME_AVAILABLE.asColoredString(), Lang.WEEKLY_DISPLAY_NAME_UNAVAILABLE.asColoredString(), Lang.WEEKLY_PREMIUM_DISPLAY_NAME_UNAVAILABLE.asColoredString(), Lang.WEEKLY_AVAILABLE_LORE.asReplacedList(Collections.emptyMap()), Lang.WEEKLY_AVAILABLE_PREMIUM_LORE.asReplacedList(Collections.emptyMap()), Lang.WEEKLY_UNAVAILABLE_LORE.asReplacedList(Collections.emptyMap()), Lang.WEEKLY_PREMIUM_UNAVAILABLE_LORE.asReplacedList(Collections.emptyMap()), Config.WEEKLY_REWARDS.asReplacedList(Collections.emptyMap()), Config.WEEKLY_PREMIUM_REWARDS.asReplacedList(Collections.emptyMap())));
        if (Config.MONTHLY_ENABLED.asBoolean())
            rewards.add(new Reward(RewardType.MONTHLY, Config.MONTHLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean(), Config.MONTHLY_COOLDOWN_FORMAT.asString(), Config.MONTHLY_COOLDOWN.asInt(), Config.MONTHLY_COOLDOWN_FORMAT.asString(), Config.MONTHLY_POSITION.asInt(), Config.MONTHLY_SOUND.asUppercase(), Lang.MONTHLY_TITLE.asColoredString(), Lang.MONTHLY_SUBTITLE.asColoredString(), Lang.MONTHLY_COLLECTED.asColoredString(), Lang.MONTHLY_PREMIUM_COLLECTED.asColoredString(), Config.MONTHLY_AVAILABLE_ITEM.asAnItem(), Config.MONTHLY_UNAVAILABLE_ITEM.asAnItem(), Lang.MONTHLY_DISPLAY_NAME_AVAILABLE.asColoredString(), Lang.MONTHLY_PREMIUM_DISPLAY_NAME_AVAILABLE.asColoredString(), Lang.MONTHLY_DISPLAY_NAME_UNAVAILABLE.asColoredString(), Lang.MONTHLY_PREMIUM_DISPLAY_NAME_UNAVAILABLE.asColoredString(), Lang.MONTHLY_AVAILABLE_LORE.asReplacedList(Collections.emptyMap()), Lang.MONTHLY_AVAILABLE_PREMIUM_LORE.asReplacedList(Collections.emptyMap()), Lang.MONTHLY_UNAVAILABLE_LORE.asReplacedList(Collections.emptyMap()), Lang.MONTHLY_PREMIUM_UNAVAILABLE_LORE.asReplacedList(Collections.emptyMap()), Config.MONTHLY_REWARDS.asReplacedList(Collections.emptyMap()), Config.MONTHLY_PREMIUM_REWARDS.asReplacedList(Collections.emptyMap())));

    }

    public Optional<Reward> getRewardByType(RewardType rewardType) {
        return rewards.stream().filter(reward -> reward.getRewardType() == rewardType).findFirst();
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

    public Set<Reward> getRewards() {
        return rewards;
    }
}
