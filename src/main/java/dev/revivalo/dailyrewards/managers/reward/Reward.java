package dev.revivalo.dailyrewards.managers.reward;

import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class Reward {
    private final RewardType rewardType;
    private final Config availableAfterFirstJoin;
    private final Config placeholder;
    private final Config cooldown;
    private final Config cooldownFormat;
    private final Config position;
    private final Config sound;
    private final Lang title;
    private final Lang subtitle;
    private final Lang collectedMessage;
    private final Lang collectedPremiumMessage;
    private final Config availableItem;
    private final Config unavailableItem;
    private final Lang availableDisplayName;
    private final Lang availablePremiumDisplayName;
    private final Lang unavailableDisplayName;
    private final Lang unavailablePremiumDisplayName;
    private final Lang availableLore;
    private final Lang availablePremiumLore;
    private final Lang unavailableLore;
    private final Lang unavailablePremiumLore;
    private final Config defaultRewards;
    private final Config premiumRewards;

    public Reward(RewardType rewardType,
                  Config availableAfterFirstJoin,
                  Config placeholder,
                  Config cooldown,
                  Config cooldownFormat,
                  Config position,
                  Config sound,
                  Lang title,
                  Lang subtitle,
                  Lang collectedMessage,
                  Lang collectedPremiumMessage,
                  Config availableItem,
                  Config unavailableItem,
                  Lang availableDisplayName,
                  Lang availablePremiumDisplayName,
                  Lang unavailableDisplayName,
                  Lang unavailablePremiumDisplayName,
                  Lang availableLore,
                  Lang availablePremiumLore,
                  Lang unavailableLore,
                  Lang unavailablePremiumLore,
                  Config defaultRewards,
                  Config premiumRewards) {
        this.rewardType = rewardType;
        this.availableAfterFirstJoin = availableAfterFirstJoin;
        this.placeholder = placeholder;
        this.cooldown = cooldown;
        this.cooldownFormat = cooldownFormat;
        this.position = position;
        this.sound = sound;
        this.title = title;
        this.subtitle = subtitle;
        this.collectedMessage = collectedMessage;
        this.collectedPremiumMessage = collectedPremiumMessage;
        this.availableItem = availableItem;
        this.unavailableItem = unavailableItem;
        this.availableDisplayName = availableDisplayName;
        this.availablePremiumDisplayName = availablePremiumDisplayName;
        this.unavailableDisplayName = unavailableDisplayName;
        this.unavailablePremiumDisplayName = unavailablePremiumDisplayName;
        this.availableLore = availableLore;
        this.availablePremiumLore = availablePremiumLore;
        this.unavailableLore = unavailableLore;
        this.unavailablePremiumLore = unavailablePremiumLore;
        this.defaultRewards = defaultRewards;
        this.premiumRewards = premiumRewards;
    }

    public String getRewardName() {
        return getRewardType().toString();
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public boolean isAvailableAfterFirstJoin() {
        return availableAfterFirstJoin.asBoolean();
    }

    public String getPlaceholder() {
        return placeholder.asString();
    }

    public long getCooldown() {
        return cooldown.asLong() * 60L * 60L * 1000L;
    }

    public String getCooldownFormat() {
        return cooldownFormat.asString();
    }

    public int getPosition() {
        return position.asInt();
    }

    public String getSound() {
        return sound.asString();
    }

    public String getTitle() {
        return title.asColoredString();
    }

    public String getSubtitle() {
        return subtitle.asColoredString();
    }

    public String getCollectedMessage() {
        return collectedMessage.asColoredString();
    }

    public String getCollectedPremiumMessage() {
        return collectedPremiumMessage.asColoredString();
    }

    public ItemStack getAvailableItem() {
        return availableItem.asAnItem();
    }

    public ItemStack getUnavailableItem() {
        return unavailableItem.asAnItem();
    }

    public List<String> getDefaultRewards() {
        return defaultRewards.asReplacedList(Collections.emptyMap());
    }

    public List<String> getPremiumRewards() {
        return premiumRewards.asReplacedList(Collections.emptyMap());
    }

    public String getAvailableDisplayName() {
        return availableDisplayName.asColoredString();
    }

    public String getAvailablePremiumDisplayName() {
        return availablePremiumDisplayName.asColoredString();
    }

    public String getUnavailableDisplayName() {
        return unavailableDisplayName.asColoredString();
    }

    public String getUnavailablePremiumDisplayName() {
        return unavailablePremiumDisplayName.asColoredString();
    }

    public List<String> getAvailableLore() {
        return availableLore.asReplacedList(Collections.emptyMap());
    }

    public List<String> getAvailablePremiumLore() {
        return availablePremiumLore.asReplacedList(Collections.emptyMap());
    }

    public List<String> getUnavailablePremiumLore() {
        return unavailablePremiumLore.asReplacedList(Collections.emptyMap());
    }

    public List<String> getUnavailableLore() {
        return unavailableLore.asReplacedList(Collections.emptyMap());
    }
}
