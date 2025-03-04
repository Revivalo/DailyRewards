package eu.athelion.dailyrewards.configuration.file;

import eu.athelion.dailyrewards.DailyRewardsPlugin;
import eu.athelion.dailyrewards.configuration.ColorTextModifier;
import eu.athelion.dailyrewards.configuration.PlaceholderColorTextModifier;
import eu.athelion.dailyrewards.configuration.TextModifier;
import eu.athelion.dailyrewards.configuration.YamlFile;
import eu.athelion.dailyrewards.hook.HookManager;
import eu.athelion.dailyrewards.util.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public enum Lang {
	PREFIX,
	BACK,
	LOADING,
	AVAILABLE,
	UNKNOWN_COMMAND,
	COMMAND_USAGE,
	INCOMPLETE_REWARD_RESET,
	REWARD_RESET,
	UNAVAILABLE_PLAYER,
	UNAVAILABLE_SETTING,
	REWARD_DISABLED,
	SETTING_ENABLED,
	SETTING_DISABLED,
	CLICK_TO_PERFORM,
	INSUFFICIENT_PERMISSION,

	CLAIM_COMMAND_DESCRIPTION,
	RESET_COMMAND_DESCRIPTION,
	SETTINGS_COMMAND_DESCRIPTION,
	TOGGLE_COMMAND_DESCRIPTION,
	RELOAD_COMMAND_DESCRIPTION,
	IMPORT_COMMAND_DESCRIPTION,
	HELP_COMMAND_DESCRIPTION,

	REWARDS_ARE_NOT_SET,
	RELOAD_MSG,
	MENU_TITLE,
	SETTINGS_TITLE,
	SETTINGS_DISPLAY_NAME,
	AUTO_CLAIM_FAILED,
	AUTO_CLAIM_FAILED_HOVER_TEXT,
	AUTO_CLAIM_FAILED_HOVER_TEXT_LIST_FORMAT,
	JOIN_AUTO_CLAIM_NOTIFICATION,
	JOIN_NOTIFICATION_SETTING_NAME,
	JOIN_AUTO_CLAIM_SETTING_NAME,
	JOIN_HOVER_MESSAGE,
	JOIN_NOTIFICATION,
	COOLDOWN_MESSAGE,
	CLAIMING_IN_DISABLED_WORLD,
	NOT_ENOUGH_REQUIRED_TIME_TO_CLAIM,
	LOCATED_IN_RESTRICTED_WORLD,
	INSUFFICIENT_PLAY_TIME,
	INSUFFICIENT_PERMISSIONS,
	NOT_ENOUGH_FREE_INVENTORY_SLOTS,
	UNAVAILABLE_REWARD,

	DAILY_NAME,
	DAILY_TITLE,
	DAILY_SUBTITLE,
	DAILY_COLLECTED,
	DAILY_PREMIUM_COLLECTED,
	DAILY_DISPLAYNAME_AVAILABLE,
	DAILY_AVAILABLE_LORE,
	DAILY_AVAILABLE_PREMIUM_LORE,
	DAILY_PREMIUM_DISPLAYNAME_AVAILABLE,
	DAILY_DISPLAYNAME_UNAVAILABLE,
	DAILY_PREMIUM_DISPLAYNAME_UNAVAILABLE,

	DAILY_UNAVAILABLE_LORE,
	DAILY_UNAVAILABLE_PREMIUM_LORE,

	WEEKLY_NAME,
	WEEKLY_TITLE,
	WEEKLY_SUBTITLE,
	WEEKLY_COLLECTED,
	WEEKLY_PREMIUM_COLLECTED,
	WEEKLY_DISPLAYNAME_AVAILABLE,
	WEEKLY_PREMIUM_DISPLAYNAME_AVAILABLE,

	WEEKLY_AVAILABLE_LORE,
	WEEKLY_AVAILABLE_PREMIUM_LORE,
	WEEKLY_DISPLAYNAME_UNAVAILABLE,
	WEEKLY_PREMIUM_DISPLAYNAME_UNAVAILABLE,

	WEEKLY_UNAVAILABLE_LORE,
	WEEKLY_UNAVAILABLE_PREMIUM_LORE,

	MONTHLY_NAME,
	MONTHLY_TITLE,
	MONTHLY_SUBTITLE,
	MONTHLY_COLLECTED,
	MONTHLY_PREMIUM_COLLECTED,
	MONTHLY_DISPLAYNAME_AVAILABLE,
	MONTHLY_PREMIUM_DISPLAYNAME_AVAILABLE,

	MONTHLY_AVAILABLE_LORE,
	MONTHLY_AVAILABLE_PREMIUM_LORE,
	MONTHLY_DISPLAYNAME_UNAVAILABLE,
	MONTHLY_PREMIUM_DISPLAYNAME_UNAVAILABLE,
	MONTHLY_UNAVAILABLE_LORE,
	MONTHLY_UNAVAILABLE_PREMIUM_LORE,
	FULL_INVENTORY_MESSAGE;

    private static final Map<String, String> messages = new HashMap<>();
	private static final Map<String, String> listsStoredAsStrings = new HashMap<>();

	private static TextModifier textModifier;

	private String getName() {
		return this.name();
	}

	public static void reload(Config language) {
        YamlFile langYamlFile = new YamlFile("lang/" + language.asString() + ".yml",
                DailyRewardsPlugin.get().getDataFolder(), YamlFile.UpdateMethod.EVERYTIME);

		textModifier = DailyRewardsPlugin.getTextModifier();

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
					String editedKey = key.toUpperCase(Locale.ENGLISH).replace("-", "_");
					if (langSection.isList(key)) {
						listsStoredAsStrings.put(editedKey, String.join("ᴪ", langSection.getStringList(key)));
					} else
						messages.put(editedKey, Objects.requireNonNull(langSection.getString(key)).replace("%prefix%", Lang.PREFIX.asColoredString()));
				});
	}

	public List<String> asReplacedList(final Map<String, String> definitions) {
		return TextUtil.colorize(TextUtil.replaceListAsString(listsStoredAsStrings.get(getName()), definitions));
	}

	public String asColoredString() {
		return textModifier.modifyText(null, messages.get(getName()));
	}

	public String asColoredString(Player player) {
		return textModifier.modifyText(player, messages.get(getName()));
	}

	public String asReplacedString(Player player, Map<String, String> definitions) {
		return textModifier.modifyText(player, TextUtil.replaceString(messages.get(getName()), definitions));
	}
}