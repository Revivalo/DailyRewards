package cz.revivalo.dailyrewards.configuration.enums;

import com.google.common.base.Splitter;
import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.configuration.YamlFile;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
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
						final List<String> coloredList = new ArrayList<>();
						for (final String uncoloredLine : configuration.getStringList("lang." + key)){
							coloredList.add(applyColor(uncoloredLine));
						}
						listsAsStrings.put(key, String.join("⎶", coloredList));
						return;
					}
					messages.put(key, applyColor(configuration.getString("lang." + key)));
				});
	}

	public static String applyColor(String message){
		Matcher matcher = hexPattern.matcher(message);
		while (matcher.find()){
			String color = message.substring(matcher.start(), matcher.end());
			message = message.replace(color, ChatColor.of(color.replace("<", "").replace(">", "")) + "");
			matcher = hexPattern.matcher(message);
		}
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static void sendListToPlayer(final Player player, final List<String> list) {
		list.forEach(player::sendMessage);
	}

	public List<String> asColoredList(final Map<String, String> definitions) {
		final String loreAsString = listsAsStrings.get(this.text);
		final String[] keys = definitions.keySet().toArray(new String[0]);
		final String[] values = definitions.values().toArray(new String[0]);

		return Splitter.on("⎶").splitToList(StringUtils.replaceEach(loreAsString, keys, values));
	}
		/*final StringBuilder sb = new StringBuilder( loreAsString.length() << 1 );

		final Trie.TrieBuilder builder = Trie.builder();
		builder.onlyWholeWords();
		builder.ignoreOverlaps();

		final String[] keys = definitions.keySet().toArray(new String[0]);

		for( final String key : keys ) {
			builder.addKeyword( key );
		}

		final Trie trie = builder.build();
		final Collection<Emit> emits = trie.parseText( loreAsString );

		int prevIndex = 0;

		for( final Emit emit : emits ) {
			final int matchIndex = emit.getStart();

			sb.append(loreAsString, prevIndex, matchIndex);
			sb.append( definitions.get( emit.getKeyword() ) );
			prevIndex = emit.getEnd() + 1;
		}

		// Add the remainder of the string (contains no more matches).
		sb.append( loreAsString.substring( prevIndex ) );*/

		//return Splitter.on("⎶").splitToList(sb.toString());

	/*public List<String> asColoredList(String... replacements) {
		final List<String> newList = new ArrayList<>();
		for (final String line : lists.get(this.text)) {
			String newLine = line;
			for (int i = 0; i < replacements.length; i += 2)
				newLine = newLine.replace(replacements[i], replacements[i + 1]);

			newList.add(Lang.applyColor(newLine));
		}
		return newList;
	}*/

	public String asPlaceholderReplacedText(final Player player) {
		return DailyRewards.isPapiInstalled() ? PlaceholderAPI.setPlaceholders(player, messages.get(text)) : messages.get(text);
	}

	public String asColoredString() {
		return messages.get(text);
	}
}