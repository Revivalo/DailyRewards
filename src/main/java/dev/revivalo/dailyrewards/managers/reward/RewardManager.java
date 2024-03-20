package dev.revivalo.dailyrewards.managers.reward;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.api.events.AutoClaimEvent;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.actions.AutoClaimAction;
import dev.revivalo.dailyrewards.user.User;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RewardManager {
    private final Set<Reward> rewards;

    public RewardManager() {
        this.rewards = new HashSet<>();
        loadRewards();
    }

    public boolean processAutoClaimForUser(User user) {
        if (!user.hasEnabledAutoClaim()) {
            return false;
        }

        if (user.getAvailableRewards().isEmpty()) {
            return false;
        }

        AutoClaimEvent autoClaimEvent = new AutoClaimEvent(user.getPlayer(), user.getAvailableRewards());
        Bukkit.getPluginManager().callEvent(autoClaimEvent);

        if (autoClaimEvent.isCancelled()) {
            return false;
        }

        Bukkit.getScheduler().runTaskLater(DailyRewardsPlugin.get(), () ->
                new AutoClaimAction()
                        .preCheck(user.getPlayer(), user.getAvailableRewards()), Config.JOIN_AUTO_CLAIM_DELAY.asInt() * 20L);
        return false;

    }

    public void loadRewards() {
        rewards.clear();
        if (Config.DAILY_ENABLED.asBoolean())
            rewards.add(new Reward(RewardType.DAILY,
                    Config.DAILY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean(),
                    Config.DAILY_COOLDOWN_FORMAT.asString(),
                    Config.DAILY_COOLDOWN.asInt(),
                    Config.DAILY_COOLDOWN_FORMAT.asString(),
                    Config.DAILY_POSITION.asInt(),
                    Config.DAILY_SOUND.asUppercase(),
                    Lang.DAILY_TITLE.asColoredString(),
                    Lang.DAILY_SUBTITLE.asColoredString(),
                    Lang.DAILY_COLLECTED.asColoredString(),
                    Lang.DAILY_PREMIUM_COLLECTED.asColoredString(),
                    Config.DAILY_AVAILABLE_ITEM.asAnItem(),
                    Config.DAILY_UNAVAILABLE_ITEM.asAnItem(),
                    Lang.DAILY_DISPLAY_NAME_AVAILABLE.asColoredString(),
                    Lang.DAILY_PREMIUM_DISPLAY_NAME_AVAILABLE.asColoredString(),
                    Lang.DAILY_DISPLAY_NAME_UNAVAILABLE.asColoredString(),
                    Lang.DAILY_PREMIUM_DISPLAY_NAME_UNAVAILABLE.asColoredString(),
                    Lang.DAILY_AVAILABLE_LORE.asReplacedList(Collections.emptyMap()),
                    Lang.DAILY_AVAILABLE_PREMIUM_LORE.asReplacedList(Collections.emptyMap()),
                    Lang.DAILY_UNAVAILABLE_LORE.asReplacedList(Collections.emptyMap()),
                    Lang.DAILY_PREMIUM_UNAVAILABLE_LORE.asReplacedList(Collections.emptyMap()),
                    Config.DAILY_REWARDS.asReplacedList(Collections.emptyMap()),
                    Config.DAILY_PREMIUM_REWARDS.asReplacedList(Collections.emptyMap())));
        if (Config.WEEKLY_ENABLED.asBoolean())
            rewards.add(new Reward(RewardType.WEEKLY,
                    Config.WEEKLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean(),
                    Config.WEEKLY_COOLDOWN_FORMAT.asString(),
                    Config.WEEKLY_COOLDOWN.asInt(),
                    Config.WEEKLY_COOLDOWN_FORMAT.asString(),
                    Config.WEEKLY_POSITION.asInt(),
                    Config.WEEKLY_SOUND.asUppercase(),
                    Lang.WEEKLY_TITLE.asColoredString(),
                    Lang.WEEKLY_SUBTITLE.asColoredString(),
                    Lang.WEEKLY_COLLECTED.asColoredString(),
                    Lang.WEEKLY_PREMIUM_COLLECTED.asColoredString(),
                    Config.WEEKLY_AVAILABLE_ITEM.asAnItem(),
                    Config.WEEKLY_UNAVAILABLE_ITEM.asAnItem(),
                    Lang.WEEKLY_DISPLAY_NAME_AVAILABLE.asColoredString(),
                    Lang.WEEKLY_PREMIUM_DISPLAY_NAME_AVAILABLE.asColoredString(),
                    Lang.WEEKLY_DISPLAY_NAME_UNAVAILABLE.asColoredString(),
                    Lang.WEEKLY_PREMIUM_DISPLAY_NAME_UNAVAILABLE.asColoredString(),
                    Lang.WEEKLY_AVAILABLE_LORE.asReplacedList(Collections.emptyMap()),
                    Lang.WEEKLY_AVAILABLE_PREMIUM_LORE.asReplacedList(Collections.emptyMap()),
                    Lang.WEEKLY_UNAVAILABLE_LORE.asReplacedList(Collections.emptyMap()),
                    Lang.WEEKLY_PREMIUM_UNAVAILABLE_LORE.asReplacedList(Collections.emptyMap()),
                    Config.WEEKLY_REWARDS.asReplacedList(Collections.emptyMap()),
                    Config.WEEKLY_PREMIUM_REWARDS.asReplacedList(Collections.emptyMap())));
        if (Config.MONTHLY_ENABLED.asBoolean())
            rewards.add(new Reward(
                    RewardType.MONTHLY,
                    Config.MONTHLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean(),
                    Config.MONTHLY_COOLDOWN_FORMAT.asString(),
                    Config.MONTHLY_COOLDOWN.asInt(),
                    Config.MONTHLY_COOLDOWN_FORMAT.asString(),
                    Config.MONTHLY_POSITION.asInt(),
                    Config.MONTHLY_SOUND.asUppercase(),
                    Lang.MONTHLY_TITLE.asColoredString(),
                    Lang.MONTHLY_SUBTITLE.asColoredString(),
                    Lang.MONTHLY_COLLECTED.asColoredString(),
                    Lang.MONTHLY_PREMIUM_COLLECTED.asColoredString(),
                    Config.MONTHLY_AVAILABLE_ITEM.asAnItem(),
                    Config.MONTHLY_UNAVAILABLE_ITEM.asAnItem(),
                    Lang.MONTHLY_DISPLAY_NAME_AVAILABLE.asColoredString(),
                    Lang.MONTHLY_PREMIUM_DISPLAY_NAME_AVAILABLE.asColoredString(),
                    Lang.MONTHLY_DISPLAY_NAME_UNAVAILABLE.asColoredString(),
                    Lang.MONTHLY_PREMIUM_DISPLAY_NAME_UNAVAILABLE.asColoredString(),
                    Lang.MONTHLY_AVAILABLE_LORE.asReplacedList(Collections.emptyMap()),
                    Lang.MONTHLY_AVAILABLE_PREMIUM_LORE.asReplacedList(Collections.emptyMap()),
                    Lang.MONTHLY_UNAVAILABLE_LORE.asReplacedList(Collections.emptyMap()),
                    Lang.MONTHLY_PREMIUM_UNAVAILABLE_LORE.asReplacedList(Collections.emptyMap()),
                    Config.MONTHLY_REWARDS.asReplacedList(Collections.emptyMap()),
                    Config.MONTHLY_PREMIUM_REWARDS.asReplacedList(Collections.emptyMap())));
    }

    public Optional<Reward> getRewardByType(RewardType rewardType) {
        return rewards.stream().filter(reward -> reward.getRewardType() == rewardType).findFirst();
    }

    public String getRewardsPlaceholder(final RewardType reward) {
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