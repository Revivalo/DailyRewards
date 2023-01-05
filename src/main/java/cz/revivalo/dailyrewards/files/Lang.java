package cz.revivalo.dailyrewards.files;

import cz.revivalo.dailyrewards.DailyRewards;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    JOIN_NOTIFICATION("join-notifications"),
    COOLDOWN_MESSAGE("cooldown-message"),


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
    FULL_INVENTORY_MESSAGE("full-inventory-message"),
    AUTO_CLAIMED_NOTIFICATION("auto-claim-notification");

    private final String text;
    private static final Map<String, String> messages = new HashMap<>();
    private static final Map<String, List<String>> lists = new HashMap<>();

    Lang(final String text) {
        this.text = text;
    }
    public String asColoredString() {
        return applyColor(messages.get(text));
    }
    public String asPlaceholderApiReplacedString(final Player player) {
        if (DailyRewards.PAPI){
            return PlaceholderAPI.setPlaceholders(player, messages.get(text));
        } else {
            return messages.get(text);
        }
    }

    public List<String> asColoredList(String... replacements){
        final List<String> newList = new ArrayList<>();
        for (final String line : lists.get(this.text)){
            String newLine = line;
            for (int i = 0; i < replacements.length; i += 2){
                newLine = line.replace(replacements[i], replacements[i+1]);
            }
            newList.add(applyColor(newLine));
        }
        return newList;
    }

    /*static {
        reload();
    }*/

    public static void reload(){
        final YamlConfiguration configuration = new YAMLFile("lang" + File.separator + Config.LANGUAGE.asString() + ".yml", DailyRewards.getPlugin(DailyRewards.class).getDataFolder()).getConfiguration();
        for (String key : configuration.getConfigurationSection("lang").getKeys(true))
            if (key.endsWith("lore") || key.endsWith("notification") || key.endsWith("help")){
                lists.put(key, configuration.getStringList("lang." + key));
            } else {
                messages.put(key, applyColor(configuration.getString("lang." + key)));
            }
    }

    public static void sendListToPlayer(final Player player, final List<String> list){
        for (final String line : list){
            player.sendMessage(line);
        }
    }
    private static final Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]){6}>");
    private static String applyColor(String message){
        if (DailyRewards.isHexSupport) {
            Matcher matcher = hexPattern.matcher(message);
            while (matcher.find()) {
                final ChatColor hexColor = ChatColor.valueOf(matcher.group().substring(1, matcher.group().length() - 1));
                final String before = message.substring(0, matcher.start());
                final String after = message.substring(matcher.end());
                message = before + hexColor + after;
                matcher = hexPattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}