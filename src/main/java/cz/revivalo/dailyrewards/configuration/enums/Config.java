package cz.revivalo.dailyrewards.configuration.enums;

import com.google.common.base.Splitter;
import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.configuration.YamlFile;
import dev.dbassett.skullcreator.SkullCreator;
import dev.lone.itemsadder.api.CustomStack;
import io.th0rgal.oraxen.api.OraxenItems;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public enum Config {
	MENU_SIZE("menu-size"),
	FILL_BACKGROUND("fill-background-enabled"),
	BACKGROUND_ITEM("background-item"),
	ENABLE_JOIN_NOTIFICATION("enable-join-notification"),
	ANNOUNCE_ENABLED("announce-enabled"),
	JOIN_NOTIFICATION_DELAY("join-notification-delay"),
	UNAVAILABLE_REWARD_SOUND("unavailable-reward-sound"),
	AUTO_CLAIM_REWARDS_ON_JOIN("auto-claim-rewards-on-join"),

	USE_MYSQL("use-mysql"),
	MYSQL_IP("mysql-ip"),
	MYSQL_DBNAME("mysql-database-name"),
	MYSQL_USERNAME("mysql-username"),
	MYSQL_PASSWORD("mysql-password"),
	UPDATE_CHECKER("update-checker"),

	DAILY_ENABLED("daily-enabled"),
	DAILY_COOLDOWN("daily-cooldown"),
	DAILY_COOLDOWN_FORMAT("daily-cooldown-format"),
	DAILY_AVAILABLE_AFTER_FIRST_JOIN("daily-available-after-first-join"),
	DAILY_PLACEHOLDER("daily-placeholder"),
	DAILY_POSITION("daily-position"),
	DAILY_SOUND("daily-sound"),
	DAILY_AVAILABLE_ITEM("daily-available-item"),
	DAILY_UNAVAILABLE_ITEM("daily-unavailable-item"),
	DAILY_REWARDS("daily-rewards"),
	DAILY_PREMIUM_REWARDS("daily-premium-rewards"),

	WEEKLY_ENABLED("weekly-enabled"),
	WEEKLY_COOLDOWN("weekly-cooldown"),
	WEEKLY_COOLDOWN_FORMAT("weekly-cooldown-format"),
	WEEKLY_AVAILABLE_AFTER_FIRST_JOIN("weekly-available-after-first-join"),
	WEEKLY_PLACEHOLDER("weekly-placeholder"),
	WEEKLY_AVAILABLE_ITEM("weekly-available-item"),
	WEEKLY_UNAVAILABLE_ITEM("weekly-unavailable-item"),
	WEEKLY_POSITION("weekly-position"),
	WEEKLY_SOUND("weekly-sound"),
	WEEKLY_REWARDS("weekly-rewards"),
	WEEKLY_PREMIUM_REWARDS("weekly-premium-rewards"),

	MONTHLY_ENABLED("monthly-enabled"),
	MONTHLY_COOLDOWN("monthly-cooldown"),
	MONTHLY_COOLDOWN_FORMAT("monthly-cooldown-format"),
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
	private static final Map<String, String> listsStoredAsStrings = new HashMap<>();
	private static final Map<String, ItemStack> items = new HashMap<>();

	static {
		reload();
	}

	private final String text;

	/*public static void reload() {
		final YamlConfiguration configuration = new YamlFile("config.yml", DailyRewards.getPlugin(DailyRewards.class).getDataFolder()).getConfiguration();
		for (String key : configuration.getConfigurationSection("config").getKeys(true)) {
			if (key.endsWith("lore") || key.endsWith("rewards") || key.endsWith("notifications") || key.endsWith("help")) {
				lists.put(key, configuration.getStringList("config." + key));
			} else {
				messages.put(key, configuration.getString("config." + key));
			}
		}
		Lang.reload();
	}*/

	public static void reload() {
		final YamlConfiguration configuration = new YamlFile("config.yml",
				DailyRewards.getPlugin().getDataFolder())
				.getConfiguration();

		final ConfigurationSection configurationSection = configuration.getConfigurationSection("config");
		Objects.requireNonNull(configurationSection)
				.getKeys(false)
				.forEach(key -> {
					if (key.endsWith("lore") || key.endsWith("rewards") || key.endsWith("notifications") || key.endsWith("help")) {
						listsStoredAsStrings.put(key, String.join("⎶", configurationSection.getStringList(key)));
						return;
					} else if (key.endsWith("item")){
						final String itemName = configurationSection.getString(key);
						if (itemName.length() > 64){
							items.put(key, SkullCreator.itemFromBase64(itemName));
						} else if (DailyRewards.isItemsAdderInstalled() && CustomStack.getInstance(itemName) != null){
							items.put(key, CustomStack.getInstance(itemName).getItemStack());
						} else if (DailyRewards.isOraxenInstalled() && OraxenItems.exists(itemName)){
							items.put(key, OraxenItems.getItemById(itemName).build());
						} else {
							items.put(key, new ItemStack(Material.valueOf(itemName.toUpperCase(Locale.ENGLISH))));
						}
					}
					messages.put(key, configurationSection.getString(key));
				});
		Lang.reload();
	}

	public static String formatTime(String message, long remainingTime) {
		return replaceString(message, new HashMap<String, String>(){{
			put("%days%", String.valueOf(TimeUnit.MILLISECONDS.toDays(remainingTime)));
			put("%hours%", String.valueOf(TimeUnit.MILLISECONDS.toHours(remainingTime) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(remainingTime))));
			put("%minutes%", String.valueOf(TimeUnit.MILLISECONDS.toMinutes(remainingTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime))));
			put("%seconds%", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(remainingTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime))));
		}});
	}

	public static String replaceString(String messageToReplace, final Map<String, String> definitions){
		final String[] keys = definitions.keySet().toArray(new String[0]);
		final String[] values = definitions.values().toArray(new String[0]);

		return StringUtils.replaceEach(messageToReplace, keys, values);
	}

	public List<String> asReplacedList(final Map<String, String> definitions) {
		return Splitter.on("⎶").splitToList(replaceString(listsStoredAsStrings.get(this.text), definitions));
	}

	public String asString() {
		return messages.get(text);
	}
	public ItemStack asAnItem(){
		return items.get(this.text);
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
