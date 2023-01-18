package cz.revivalo.dailyrewards.configuration.enums;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.configuration.YamlFile;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public enum Config {
	LANGUAGE("language"),
	DAILY_COOLDOWN("daily-cooldown"),
	WEEKLY_COOLDOWN("weekly-cooldown"),
	MONTHLY_COOLDOWN("monthly-cooldown"),
	MENU_SIZE("menu-size"),
	FILL_BACKGROUND("fill-background-enabled"),
	BACKGROUND_ITEM("background-item"),
	ENABLE_JOIN_NOTIFICATION("enable-join-notification"),
	ANNOUNCE_ENABLED("announce-enabled"),
	JOIN_NOTIFICATION_DELAY("join-notification-delay"),
	COOL_DOWN_FORMAT("cooldown-format"),
	UNAVAILABLE_REWARD_SOUND("unavailable-reward-sound"),
	AUTO_CLAIM_REWARDS_ON_JOIN("auto-claim-rewards-on-join"),

	USE_MYSQL("use-mysql"),
	MYSQL_IP("mysql-ip"),
	MYSQL_DBNAME("mysql-database-name"),
	MYSQL_USERNAME("mysql-username"),
	MYSQL_PASSWORD("mysql-password"),
	UPDATE_CHECKER("update-checker"),

	DAILY_ENABLED("daily-enabled"),
	DAILY_AVAILABLE_AFTER_FIRST_JOIN("daily-available-after-first-join"),
	DAILY_PLACEHOLDER("daily-placeholder"),
	DAILY_POSITION("daily-position"),
	DAILY_SOUND("daily-sound"),
	DAILY_AVAILABLE_ITEM("daily-available-item"),
	DAILY_UNAVAILABLE_ITEM("daily-unavailable-item"),
	DAILY_REWARDS("daily-rewards"),
	DAILY_PREMIUM_REWARDS("daily-premium-rewards"),

	WEEKLY_ENABLED("weekly-enabled"),
	WEEKLY_AVAILABLE_AFTER_FIRST_JOIN("weekly-available-after-first-join"),
	WEEKLY_PLACEHOLDER("weekly-placeholder"),
	WEEKLY_AVAILABLE_ITEM("weekly-available-item"),
	WEEKLY_UNAVAILABLE_ITEM("weekly-unavailable-item"),
	WEEKLY_POSITION("weekly-position"),
	WEEKLY_SOUND("weekly-sound"),
	WEEKLY_REWARDS("weekly-rewards"),
	WEEKLY_PREMIUM_REWARDS("weekly-premium-rewards"),

	MONTHLY_ENABLED("monthly-enabled"),
	MONTHLY_AVAILABLE_AFTER_FIRST_JOIN("monthly-available-after-first-join"),
	MONTHLY_PLACEHOLDER("monthly-placeholder"),
	MONTHLY_AVAILABLE_ITEM("monthly-available-item"),
	MONTHLY_UNAVAILABLE_ITEM("monthly-unavailable-item"),
	MONTHLY_POSITION("monthly-position"),
	MONTHLY_SOUND("monthly-sound"),
	MONTHLY_REWARDS("monthly-rewards"),
	MONTHLY_PREMIUM_REWARDS("monthly-premium-rewards"),
	CHECK_FOR_FULL_INVENTORY("check-for-full-inventory");

	private static final Map<String, String> messages = new HashMap<>();
	private static final Map<String, List<String>> lists = new HashMap<>();

	static {
		reload();
	}

	private final String text;

	public static String format(long cd) {
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(cd),
				TimeUnit.MILLISECONDS.toMinutes(cd) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(cd)),
				TimeUnit.MILLISECONDS.toSeconds(cd) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(cd)));
	}


	public static void reload() {
		final YamlConfiguration configuration = new YamlFile("config.yml", DailyRewards.getPlugin(DailyRewards.class).getDataFolder()).getConfiguration();
		for (String key : configuration.getConfigurationSection("config").getKeys(true)) {
			if (key.endsWith("lore") || key.endsWith("rewards") || key.endsWith("notifications") || key.endsWith("help")) {
				lists.put(key, configuration.getStringList("config." + key));
			} else {
				messages.put(key, configuration.getString("config." + key));
			}
		}
		Lang.reload();
	}
	/*public static void reload() {
		final YamlConfiguration configuration = new YamlFile("config.yml",
				DailyRewards.getPlugin(DailyRewards.class).getDataFolder())
				.getConfiguration();

		Objects.requireNonNull(configuration.getConfigurationSection("config"))
				.getKeys(true)
				.forEach(key -> {
					if (key.endsWith("lore") || key.endsWith("rewards") || key.endsWith("notifications") || key.endsWith("help")) {
						lists.put(key, configuration.getStringList("config." + key));
						return;
					}
					messages.put(key, configuration.getString("config." + key));
				});
		Lang.reload();
	}*/

	public List<String> asReplacedStringList(String... replacements) {
		final List<String> newList = new ArrayList<>();
		for (final String line : lists.get(this.text)) {
			String newLine = line;
			for (int i = 0; i < replacements.length; i += 2)
				newLine = line.replace(replacements[i], replacements[i + 1]);

			newList.add(newLine);
		}
		return newList;
	}

	public String asString() {
		return messages.get(text);
	}
	public String asUppercase() {
		return this.asString().toUpperCase();
	}

	public boolean asBoolean() {
		return Boolean.parseBoolean(messages.get(text));
	}

	public long asLong() {
		return Long.parseLong(messages.get(text)) * 3600000;
	}

	public int asInt() {
		return Integer.parseInt(messages.get(text));
	}
}
