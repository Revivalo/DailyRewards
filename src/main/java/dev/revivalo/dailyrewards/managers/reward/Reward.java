package dev.revivalo.dailyrewards.managers.reward;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Reward {
    private final RewardType rewardType;
    private final boolean availableAfterFirstJoin;
    private final String placeholder;
    private final long cooldown;
    private final String cooldownFormat;
    private final int position;
    private final String sound;
    private final String title;
    private final String subtitle;
    private final String collectedMessage;
    private final String collectedPremiumMessage;
    private final ItemStack availableItem;
    private final ItemStack unavailableItem;
    private final String availableDisplayName;
    private final String availablePremiumDisplayName;
    private final String unavailableDisplayName;
    private final List<String> availableLore;
    private final List<String> availablePremiumLore;
    private final List<String> unavailableLore;
    private final List<String> unavailablePremiumLore;
    private final List<String> defaultRewards;
    private final List<String> premiumRewards;

    public Reward(RewardType rewardType,
                  boolean availableAfterFirstJoin,
                  String placeholder,
                  long cooldown,
                  String cooldownFormat,
                  int position,
                  String sound,
                  String title,
                  String subtitle,
                  String collectedMessage,
                  String collectedPremiumMessage,
                  ItemStack availableItem,
                  ItemStack unavailableItem,
                  String availableDisplayName,
                  String availablePremiumDisplayName,
                  String unavailableDisplayName,
                  List<String> availableLore,
                  List<String> availablePremiumLore,
                  List<String> unavailableLore,
                  List<String> unavailablePremiumLore,
                  List<String> defaultRewards,
                  List<String> premiumRewards) {
        this.rewardType = rewardType;
        this.availableAfterFirstJoin = availableAfterFirstJoin;
        this.placeholder = placeholder;
        this.cooldown = cooldown * 60L * 60L * 1000L;
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
        return availableAfterFirstJoin;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public long getCooldown() {
        return cooldown;
    }

    public String getCooldownFormat() {
        return cooldownFormat;
    }

    public int getPosition() {
        return position;
    }

    public String getSound() {
        return sound;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getCollectedMessage() {
        return collectedMessage;
    }

    public String getCollectedPremiumMessage() {
        return collectedPremiumMessage;
    }

    public ItemStack getAvailableItem() {
        return availableItem;
    }

    public ItemStack getUnavailableItem() {
        return unavailableItem;
    }

    public List<String> getDefaultRewards() {
        return defaultRewards;
    }

    public List<String> getPremiumRewards() {
        return premiumRewards;
    }

    public String getAvailableDisplayName() {
        return availableDisplayName;
    }

    public String getAvailablePremiumDisplayName() {
        return availablePremiumDisplayName;
    }

    public String getUnavailableDisplayName() {
        return unavailableDisplayName;
    }

    public List<String> getAvailableLore() {
        return availableLore;
    }

    public List<String> getAvailablePremiumLore() {
        return availablePremiumLore;
    }

    public List<String> getUnavailablePremiumLore() {
        return unavailablePremiumLore;
    }

    public List<String> getUnavailableLore() {
        return unavailableLore;
    }
}
