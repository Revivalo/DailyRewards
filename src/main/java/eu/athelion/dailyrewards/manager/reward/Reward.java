package eu.athelion.dailyrewards.manager.reward;

import eu.athelion.dailyrewards.configuration.file.Config;
import eu.athelion.dailyrewards.configuration.file.Lang;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Reward {
    private final RewardType rewardType;
    private final Config availableAfterFirstJoin, placeholder, cooldown, cooldownFormat, position, sound, availableItem, unavailableItem, defaultRewards, premiumRewards;
    private final Lang title, subtitle, collectedMessage, collectedPremiumMessage, availableDisplayName, availablePremiumDisplayName, unavailableDisplayName, unavailablePremiumDisplayName, availableLore, availablePremiumLore, unavailableLore, unavailablePremiumLore;

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

    public String getName() {
        return rewardType.toString();
    }

    public RewardType getType() {
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

    public List<Integer> getPosition() {
        return position.asReplacedList().stream().map(Integer::parseInt).collect(Collectors.toList());
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

    public String getCollectedMessage(Player player) {
        return collectedMessage.asColoredString(player);
    }

    public String getCollectedPremiumMessage(Player player) {
        return collectedPremiumMessage.asColoredString(player);
    }

    public ItemStack getAvailableItem() {
        return availableItem.asAnItem();
    }

    public ItemStack getUnavailableItem() {
        return unavailableItem.asAnItem();
    }

    public List<String> getDefaultRewards() {
        return defaultRewards.asReplacedList();
    }

    public List<String> getPremiumRewards() {
        return premiumRewards.asReplacedList();
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
