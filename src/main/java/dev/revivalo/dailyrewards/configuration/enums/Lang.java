package dev.revivalo.dailyrewards.configuration.enums;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.YamlFile;
import dev.revivalo.dailyrewards.hooks.Hooks;
import dev.revivalo.dailyrewards.utils.TextUtils;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public enum Lang {
	HELP_MESSAGE("help"),
	VALID_COMMAND_USAGE("command-usage"),
	INCOMPLETE_REWARD_RESET("incomplete-reward-reset"),
	REWARD_RESET("reward-reset"),
	UNAVAILABLE_PLAYER("unavailable-player"),
	DISABLED_REWARD("reward-disabled"),
	PERMISSION_MESSAGE("permission-msg"),
	REWARDS_IS_NOT_SET("rewards-are-not-set"),
	RELOAD_MESSAGE("reload-msg"),
	MENU_TITLE("menu-title"),
	JOIN_HOVER_MESSAGE("join-hover-message"),
	JOIN_NOTIFICATION("join-notification"),
	COOLDOWN_MESSAGE("cooldown-message"),
	AUTO_CLAIMED_NOTIFICATION("auto-claim-notification"),

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
	FULL_INVENTORY_MESSAGE("full-inventory-message");

	private static final Map<String, String> messages = new HashMap<>();
	private static final Map<String, String> listsAsStrings = new HashMap<>();
	private final String text;

	public static void reload() {
		final YamlConfiguration configuration = new YamlFile("lang.yml",
				DailyRewardsPlugin.get().getDataFolder())
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
						return;
					}
					messages.put(key, TextUtils.applyColor(langSection.getString(key)));
				});
	}

	public List<String> asReplacedList(final Map<String, String> definitions) {
		return TextUtils.replaceList(listsAsStrings.get(this.text), definitions);
	}

	public String asPlaceholderReplacedText(final Player player) {
		return Hooks.getPLACEHOLDER_API_HOOK().isOn() ? PlaceholderAPI.setPlaceholders(player, messages.get(text)) : messages.get(text);
	}

	public String asColoredString() {
		return messages.get(text);
	}
}