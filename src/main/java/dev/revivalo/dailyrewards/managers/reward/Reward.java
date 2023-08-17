package dev.revivalo.dailyrewards.managers.reward;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Reward {
    private final RewardType rewardType;
    private final boolean availableAfterFirstJoin;
    private final String placeholder;
    private final int cooldown;
    private final String cooldownFormat;
    private final int position;
    private final ItemStack availableItem;
    private final ItemStack unavailableItem;
    private final String availableDisplayName;
    private final String unavailableDisplayName;
    private final List<String> availableLore;
    private final List<String> unavailableLore;
    private final List<String> defaultRewards;
    private final List<String> premiumRewards;

    public Reward(RewardType rewardType,
                  boolean availableAfterFirstJoin,
                  String placeholder,
                  int cooldown,
                  String cooldownFormat,
                  int position,
                  ItemStack availableItem,
                  ItemStack unavailableItem,
                  String availableDisplayName,
                  String unavailableDisplayName,
                  List<String> availableLore,
                  List<String> unavailableLore,
                  List<String> defaultRewards,
                  List<String> premiumRewards) {
        this.rewardType = rewardType;
        this.availableAfterFirstJoin = availableAfterFirstJoin;
        this.placeholder = placeholder;
        this.cooldown = cooldown * 60 * 60 * 1000;
        this.cooldownFormat = cooldownFormat;
        this.position = position;
        this.availableItem = availableItem;
        this.unavailableItem = unavailableItem;
        this.availableDisplayName = availableDisplayName;
        this.unavailableDisplayName = unavailableDisplayName;
        this.availableLore = availableLore;
        this.unavailableLore = unavailableLore;
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

    public int getCooldown() {
        return cooldown;
    }

    public String getCooldownFormat() {
        return cooldownFormat;
    }

    public int getPosition() {
        return position;
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

    public String getUnavailableDisplayName() {
        return unavailableDisplayName;
    }

    public List<String> getAvailableLore() {
        return availableLore;
    }

    public List<String> getUnavailableLore() {
        return unavailableLore;
    }
}
