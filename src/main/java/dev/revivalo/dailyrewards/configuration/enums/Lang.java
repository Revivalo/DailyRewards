package dev.revivalo.dailyrewards.configuration.enums;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.ColorTextModifier;
import dev.revivalo.dailyrewards.configuration.PlaceholderColorTextModifier;
import dev.revivalo.dailyrewards.configuration.TextModifier;
import dev.revivalo.dailyrewards.configuration.YamlFile;
import dev.revivalo.dailyrewards.hooks.Hooks;
import dev.revivalo.dailyrewards.utils.TextUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Lang {
	PREFIX("prefix"),
	HELP_MESSAGE("help"),
	BACK("back"),
	LOADING("loading"),
	VALID_COMMAND_USAGE("command-usage"),
	INCOMPLETE_REWARD_RESET("incomplete-reward-reset"),
	REWARD_RESET("reward-reset"),
	UNAVAILABLE_PLAYER("unavailable-player"),
	UNAVAILABLE_SETTING("unavailable-setting"),
	DISABLED_REWARD("reward-disabled"),
	SETTING_ENABLED("setting-enabled"),
	SETTING_DISABLED("setting-disabled"),
	INSUFFICIENT_PERMISSION_MESSAGE("permission-msg"),
	REWARDS_ARE_NOT_SET("rewards-are-not-set"),
	RELOAD_MESSAGE("reload-msg"),
	MENU_TITLE("menu-title"),
	SETTINGS_TITLE("settings-title"),
	SETTINGS_DISPLAY_NAME("settings-display-name"),
	AUTO_CLAIM_FAILED("auto-claim-failed"),
	AUTO_CLAIM_FAILED_HOVER_TEXT("auto-claim-failed-hover-text"),
	AUTO_CLAIM_FAILED_HOVER_TEXT_LIST_FORMAT("auto-claim-failed-hover-text-list-format"),
	JOIN_AUTO_CLAIMED_NOTIFICATION("join-auto-claim-notification"),
	JOIN_NOTIFICATION_SETTING_NAME("join-notification-setting-name"),
	JOIN_AUTO_CLAIM_SETTING_NAME("join-auto-claim-setting-name"),
	NO_PERMISSION_SETTING_DISPLAY_NAME("no-permission-setting-display-name"),
	NO_PERMISSION_SETTING_LORE("no-permission-setting-lore"),
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
	NOT_ENOUGH_REQUIRED_TIME_TO_CLAIM("not-enough-required-time-to-claim"),
	LOCATED_IN_RESTRICTED_WORLD("located-in-restricted-world"),
	INSUFFICIENT_PLAY_TIME("insufficient-play-time"),
	INSUFFICIENT_PERMISSIONS("insufficient-permissions"),
	NOT_ENOUGH_FREE_INVENTORY_SLOTS("not-enough-free-inventory-slots"),
	UNAVAILABLE_REWARD("unavailable-reward"),

	DAILY_NAME("daily-name"),
	DAILY_TITLE("daily-title"),
	DAILY_SUBTITLE("daily-subtitle"),
	DAILY_COLLECTED("daily-collected"),
	DAILY_PREMIUM_COLLECTED("daily-premium-collected"),
	DAILY_DISPLAY_NAME_AVAILABLE("daily-displayname-available"),
	DAILY_AVAILABLE_LORE("daily-available-lore"),
	DAILY_AVAILABLE_PREMIUM_LORE("daily-available-premium-lore"),
	DAILY_PREMIUM_DISPLAY_NAME_AVAILABLE("daily-premium-displayname-available"),
	DAILY_DISPLAY_NAME_UNAVAILABLE("daily-displayname-unavailable"),
	DAILY_PREMIUM_DISPLAY_NAME_UNAVAILABLE("daily-premium-displayname-unavailable"),

	DAILY_UNAVAILABLE_LORE("daily-unavailable-lore"),
	DAILY_PREMIUM_UNAVAILABLE_LORE("daily-unavailable-premium-lore"),

	WEEKLY_NAME("weekly-name"),
	WEEKLY_TITLE("weekly-title"),
	WEEKLY_SUBTITLE("weekly-subtitle"),
	WEEKLY_COLLECTED("weekly-collected"),
	WEEKLY_PREMIUM_COLLECTED("weekly-premium-collected"),
	WEEKLY_DISPLAY_NAME_AVAILABLE("weekly-displayname-available"),
	WEEKLY_PREMIUM_DISPLAY_NAME_AVAILABLE("weekly-premium-displayname-available"),

	WEEKLY_AVAILABLE_LORE("weekly-available-lore"),
	WEEKLY_AVAILABLE_PREMIUM_LORE("weekly-available-premium-lore"),
	WEEKLY_DISPLAY_NAME_UNAVAILABLE("weekly-displayname-unavailable"),
	WEEKLY_PREMIUM_DISPLAY_NAME_UNAVAILABLE("weekly-premium-displayname-unavailable"),

	WEEKLY_UNAVAILABLE_LORE("weekly-unavailable-lore"),
	WEEKLY_PREMIUM_UNAVAILABLE_LORE("weekly-unavailable-premium-lore"),

	MONTHLY_NAME("monthly-name"),
	MONTHLY_TITLE("monthly-title"),
	MONTHLY_SUBTITLE("monthly-subtitle"),
	MONTHLY_COLLECTED("monthly-collected"),
	MONTHLY_PREMIUM_COLLECTED("monthly-premium-collected"),
	MONTHLY_DISPLAY_NAME_AVAILABLE("monthly-displayname-available"),
	MONTHLY_PREMIUM_DISPLAY_NAME_AVAILABLE("monthly-premium-displayname-available"),

	MONTHLY_AVAILABLE_LORE("monthly-available-lore"),
	MONTHLY_AVAILABLE_PREMIUM_LORE("monthly-available-premium-lore"),
	MONTHLY_DISPLAY_NAME_UNAVAILABLE("monthly-displayname-unavailable"),
	MONTHLY_PREMIUM_DISPLAY_NAME_UNAVAILABLE("monthly-premium-displayname-unavailable"),
	MONTHLY_UNAVAILABLE_LORE("monthly-unavailable-lore"),
	MONTHLY_PREMIUM_UNAVAILABLE_LORE("monthly-unavailable-premium-lore"),
	FULL_INVENTORY_MESSAGE("full-inventory-message");
	private static final YamlFile langYamlFile = new YamlFile("lang.yml",
			DailyRewardsPlugin.get().getDataFolder(), YamlFile.UpdateMethod.EVERYTIME);
	private static final Map<String, String> messages = new HashMap<>();
	private static final Map<String, String> listsStoredAsStrings = new HashMap<>();
	private final String text;
	private static TextModifier textModifier;

	Lang(String text) {
		this.text = text;
	}

	public static void reload() {
		if (Hooks.isHookEnabled(Hooks.getPlaceholderApiHook())) {
			DailyRewardsPlugin.get().getLogger().info("Using PAPI text modifier");
			textModifier = new PlaceholderColorTextModifier();
		} else textModifier = new ColorTextModifier();

		langYamlFile.reload();
		final YamlConfiguration configuration = langYamlFile.getConfiguration();

		ConfigurationSection langSection = configuration.getConfigurationSection("lang");
		if (langSection == null) {
			DailyRewardsPlugin.get().getLogger().info("Invalid configuration in " + langYamlFile.getFilePath());
			return;
		}

		langSection
				.getKeys(false)
				.forEach(key -> {
					if (langSection.isList(key)) {
						listsStoredAsStrings.put(key, String.join("ᴪ", langSection.getStringList(key)));
					} else
						messages.put(key, StringUtils.replace(langSection.getString(key), "%prefix%", Lang.PREFIX.asColoredString(), 1));
				});
	}

	public List<String> asReplacedList(final Map<String, String> definitions) {
		return TextUtils.colorize(TextUtils.replaceListAsString(listsStoredAsStrings.get(text), definitions));
	}

	public String asColoredString() {
		return textModifier.modifyText(null, messages.get(text));
	}

	public String asColoredString(Player player) {
		return textModifier.modifyText(player, messages.get(text));
	}

	public String asReplacedString(Player player, Map<String, String> definitions) {
		return textModifier.modifyText(player, TextUtils.replaceString(messages.get(text), definitions));
	}
}