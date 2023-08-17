package dev.revivalo.dailyrewards.configuration.enums;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.YamlFile;
import dev.revivalo.dailyrewards.hooks.Hooks;
import dev.revivalo.dailyrewards.utils.TextUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public enum Lang {
	PREFIX("prefix"),
	HELP_MESSAGE("help"),
	BACK("back"),
	LOADING("loading"),
	VALID_COMMAND_USAGE("command-usage"),
	INCOMPLETE_REWARD_RESET("incomplete-reward-reset"),
	REWARD_RESET("reward-reset"),
	UNAVAILABLE_PLAYER("unavailable-player"),
	DISABLED_REWARD("reward-disabled"),
	PERMISSION_MESSAGE("permission-msg"),
	REWARDS_IS_NOT_SET("rewards-are-not-set"),
	RELOAD_MESSAGE("reload-msg"),
	MENU_TITLE("menu-title"),
	SETTINGS_TITLE("settings-title"),
	SETTINGS_DISPLAY_NAME("settings-display-name"),
	JOIN_NOTIFICATION_DISPLAY_NAME("join-notification-display-name"),
	JOIN_NOTIFICATION_ENABLED_LORE("join-notification-enabled-lore"),
	JOIN_NOTIFICATION_DISABLED_LORE("join-notification-disabled-lore"),
	AUTO_CLAIM_DISPLAY_NAME("auto-claim-display-name"),
	AUTO_CLAIM_ENABLED_LORE("auto-claim-enabled-lore"),
	AUTO_CLAIM_DISABLED_LORE("auto-claim-disabled-lore"),
	JOIN_HOVER_MESSAGE("join-hover-message"),
	JOIN_NOTIFICATION("join-notification"),
	COOLDOWN_MESSAGE("cooldown-message"),
	CLAIMING_IN_DISABLED_WORLD("claiming-in-disabled-world"),
	AUTO_CLAIMED_NOTIFICATION("auto-claim-notification"),
	NOT_ENOUGH_REQUIRED_TIME_TO_CLAIM("not-enough-required-time-to-claim"),

	DAILY_TITLE("daily-title"),
	DAILY_SUBTITLE("daily-subtitle"),
	DAILY_COLLECTED("daily-collected"),
	DAILY_PREMIUM_COLLECTED("daily-premium-collected"),
	DAILY_DISPLAY_NAME_AVAILABLE("daily-displayname-available"),
	DAILY_AVAILABLE_LORE("daily-available-lore"),
	DAILY_AVAILABLE_PREMIUM_LORE("daily-available-premium-lore"),
	DAILY_DISPLAY_NAME_UNAVAILABLE("daily-displayname-unavailable"),
	DAILY_UNAVAILABLE_LORE("daily-unavailable-lore"),

	WEEKLY_TITLE("weekly-title"),
	WEEKLY_SUBTITLE("weekly-subtitle"),
	WEEKLY_COLLECTED("weekly-collected"),
	WEEKLY_PREMIUM_COLLECTED("weekly-premium-collected"),
	WEEKLY_DISPLAY_NAME_AVAILABLE("weekly-displayname-available"),
	WEEKLY_AVAILABLE_LORE("weekly-available-lore"),
	WEEKLY_AVAILABLE_PREMIUM_LORE("weekly-available-premium-lore"),
	WEEKLY_DISPLAY_NAME_UNAVAILABLE("weekly-displayname-unavailable"),
	WEEKLY_UNAVAILABLE_LORE("weekly-unavailable-lore"),

	MONTHLY_TITLE("monthly-title"),
	MONTHLY_SUBTITLE("monthly-subtitle"),
	MONTHLY_COLLECTED("monthly-collected"),
	MONTHLY_PREMIUM_COLLECTED("monthly-premium-collected"),
	MONTHLY_DISPLAY_NAME_AVAILABLE("monthly-displayname-available"),
	MONTHLY_AVAILABLE_LORE("monthly-available-lore"),
	MONTHLY_AVAILABLE_PREMIUM_LORE("monthly-available-premium-lore"),
	MONTHLY_DISPLAY_NAME_UNAVAILABLE("monthly-displayname-unavailable"),
	MONTHLY_UNAVAILABLE_LORE("monthly-unavailable-lore"),
	FULL_INVENTORY_MESSAGE("full-inventory-message"),
	UNCLAIMED_REWARDS_NOTIFICATION_HOVER_TEXT("");

	private static final Map<String, String> messages = new HashMap<>();
	private static final Map<String, String> listsAsStrings = new HashMap<>();
	private final String text;

	Lang(String text) {
		this.text = text;
	}

	public static void reload() {
		final YamlConfiguration configuration = new YamlFile("lang.yml",
				DailyRewardsPlugin.get().getDataFolder(), YamlFile.UpdateMethod.EVERYTIME)
				.getConfiguration();

		ConfigurationSection langSection = configuration.getConfigurationSection("lang");
		Objects.requireNonNull(langSection)
				.getKeys(true)
				.forEach(key -> {
					if (key.endsWith("lore")
							|| key.endsWith("notification")
							|| key.endsWith("help")) {
						final List<String> coloredList = new ArrayList<>();
						for (final String uncoloredLine : langSection.getStringList(key)){
							coloredList.add(TextUtils.applyColor(uncoloredLine));
						}
						listsAsStrings.put(key, String.join("⎶", coloredList));
					} else
						messages.put(key, TextUtils.applyColor(StringUtils.replace(langSection.getString(key), "%prefix%", Lang.PREFIX.asColoredString(), 1)));
				});
	}

	public String asReplacedString(Map<String, String> definitions) {
		return TextUtils.replaceString(messages.get(text), definitions);
	}

	public List<String> asReplacedList(final Map<String, String> definitions) {
		return TextUtils.replaceList(listsAsStrings.get(this.text), definitions);
	}

	public String asPlaceholderReplacedText(final Player player) {
		return Hooks.getPlaceholderApiHook().isOn() ? PlaceholderAPI.setPlaceholders(player, messages.get(text)) : messages.get(text);
	}

	public String asColoredString() {
		return messages.get(text);
	}
}