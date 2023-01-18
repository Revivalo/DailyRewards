package cz.revivalo.dailyrewards.configuration.enums;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.configuration.YamlFile;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public enum Lang {
	HELP_MESSAGE("help"),
	INVALID_ARGUMENTS_MESSAGE("invalid-arguments"),
	INCOMPLETE_REWARD_RESET("incomplete-reward-reset"),
	REWARD_RESET("reward-reset"),
	UNAVAILABLE_PLAYER("unavailable-player"),
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
	MONTHLY_DISPLAYNAME_AVAILABLE("monthly-displayname-available"),
	MONTHLY_AVAILABLE_LORE("monthly-available-lore"),
	MONTHLY_AVAILABLE_PREMIUM_LORE("monthly-available-premium-lore"),
	MONTHLY_DISPLAY_NAME_UNAVAILABLE("monthly-displayname-unavailable"),
	MONTHLY_UNAVAILABLE_LORE("monthly-unavailable-lore"),
	FULL_INVENTORY_MESSAGE("full-inventory-message");

	private static final Map<String, String> messages = new HashMap<>();
	private static final Map<String, List<String>> lists = new HashMap<>();
	private static final Pattern hexPattern = Pattern.compile("<#([A-Fa-f\\d]){6}>");
	private final String text;

	public static void reload() {
		final YamlConfiguration configuration = new YamlFile("lang.yml",
				DailyRewards.getPlugin().getDataFolder())
				.getConfiguration();

		Objects.requireNonNull(configuration.getConfigurationSection("lang"))
				.getKeys(true)
				.forEach(key -> {
					if (key.endsWith("lore") || key.endsWith("notification") || key.endsWith("help")) {
						lists.put(key, configuration.getStringList("lang." + key));
						return;
					}
					messages.put(key, applyColor(configuration.getString("lang." + key)));
				});
	}

	@SuppressWarnings("deprecation")
	private static String applyColor(String message) {
		if (DailyRewards.hexSupported) {
			Matcher matcher = hexPattern.matcher(message);
			while (matcher.find()) {
				message = String.format("%s%s%s", message.substring(0, matcher.start()),
						ChatColor.valueOf(matcher.group().substring(1, matcher.group().length() - 1)),
						message.substring(matcher.end()));
				matcher = hexPattern.matcher(message);
			}
		}
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static void sendListToPlayer(final Player player, final List<String> list) {
		list.forEach(player::sendMessage);
	}

	public List<String> asColoredList(String... replacements) {
		final List<String> newList = new ArrayList<>();
		for (final String line : lists.get(this.text)) {
			String newLine = line;
			for (int i = 0; i < replacements.length; i += 2)
				newLine = line.replace(replacements[i], replacements[i + 1]);

			newList.add(Lang.applyColor(newLine));
		}
		return newList;
	}

	public String asPlaceholderReplacedText(final Player player) {
		return DailyRewards.isPapiEnabled() ? PlaceholderAPI.setPlaceholders(player, messages.get(text)) : messages.get(text);
	}

	public String asColoredString() {
		return Lang.applyColor(messages.get(text));
	}
}