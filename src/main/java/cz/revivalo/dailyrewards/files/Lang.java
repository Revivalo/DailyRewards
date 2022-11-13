package cz.revivalo.dailyrewards.files;

import cz.revivalo.dailyrewards.DailyRewards;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Lang {
    HELP_MESSAGE("help"),
    INVALID_ARGUMENTS_MESSAGE("invalid-arguments"),
    INCOMPLETE_REWARD_RESET("incomplete-reward-reset"),
    DAILY_COOLDOWN("daily-cooldown"),
    WEEKLY_COOLDOWN("weekly-cooldown"),
    MONTHLY_COOLDOWN("monthly-cooldown"),
    REWARD_RESET("reward-reset"),
    UNAVAILABLE_PLAYER("unavailable-player"),
    PERMISSION_MESSAGE("permission-msg"),
    REWARD_DONT_SET("rewards-dont-set"),
    RELOAD_MESSAGE("reload-msg"),
    MENU_TITLE("menu-title"),
    MENU_SIZE("menu-size"),
    FILL_BACKGROUND("fill-background-enabled"),
    BACKGROUND_ITEM("background-item"),
    AUTOMATICALLY_ACTIVATE("automatically-activate"),
    ENABLE_JOIN_NOTIFICATION("enable-join-notification"),
    ANNOUNCE_ENABLED("announce-enabled"),
    JOIN_NOTIFICATION_DELAY("join-notification-delay"),
    JOIN_HOVER_MESSAGE("join-hover-message"),
    JOIN_NOTIFICATION("join-notifications"),
    COOL_DOWN_FORMAT("cooldown-format"),
    COOLDOWN_MESSAGE("cooldown-message"),
    UNAVAILABLE_REWARD_SOUND("unavailable-reward-sound"),

    UPDATE_CHECKER("update-checker"),

    DAILY_PLACEHOLDER("daily-placeholder"),
    DAILY_POSITION("daily-position"),
    DAILY_SOUND("daily-sound"),
    DAILY_TITLE("daily-title"),
    DAILY_SUBTITLE("daily-subtitle"),
    DAILY_COLLECTED("daily-collected"),
    DAILY_PREMIUM_COLLECTED("daily-premium-collected"),
    DAILY_DISPLAY_NAME_AVAILABLE("daily-displayname-available"),
    DAILY_AVAILABLE_ITEM("daily-available-item"),
    DAILY_UNAVAILABLE_ITEM("daily-unavailable-item"),
    DAILY_AVAILABLE_LORE("daily-available-lore"),
    DAILY_AVAILABLE_PREMIUM_LORE("daily-available-premium-lore"),
    DAILY_DISPLAY_NAME_UNAVAILABLE("daily-displayname-unavailable"),
    DAILY_UNAVAILABLE_LORE("daily-unavailable-lore"),
    DAILY_REWARDS("daily-rewards"),
    DAILY_PREMIUM_REWARDS("daily-premium-rewards"),

    WEEKLY_PLACEHOLDER("weekly-placeholder"),
    WEEKLY_POSITION("weekly-position"),
    WEEKLY_SOUND("weekly-sound"),
    WEEKLY_TITLE("weekly-title"),
    WEEKLY_SUBTITLE("weekly-subtitle"),
    WEEKLY_COLLECTED("weekly-collected"),
    WEEKLY_PREMIUM_COLLECTED("weekly-premium-collected"),
    WEEKLY_DISPLAY_NAME_AVAILABLE("weekly-displayname-available"),
    WEEKLY_AVAILABLE_ITEM("weekly-available-item"),
    WEEKLY_UNAVAILABLE_ITEM("weekly-unavailable-item"),
    WEEKLY_AVAILABLE_LORE("weekly-available-lore"),
    WEEKLY_AVAILABLE_PREMIUM_LORE("weekly-available-premium-lore"),
    WEEKLY_DISPLAY_NAME_UNAVAILABLE("weekly-displayname-unavailable"),
    WEEKLY_UNAVAILABLE_LORE("weekly-unavailable-lore"),
    WEEKLY_REWARDS("weekly-rewards"),
    WEEKLY_PREMIUM_REWARDS("weekly-premium-rewards"),

    USE_MYSQL("use-mysql"),
    MYSQL_IP("mysql-ip"),
    MYSQL_DBNAME("mysql-database-name"),
    MYSQL_USERNAME("mysql-username"),
    MYSQL_PASSWORD("mysql-password"),

    MONTHLY_PLACEHOLDER("monthly-placeholder"),
    MONTHLY_POSITION("monthly-position"),
    MONTHLY_SOUND("monthly-sound"),
    MONTHLY_TITLE("monthly-title"),
    MONTHLY_SUBTITLE("monthly-subtitle"),
    MONTHLY_COLLECTED("monthly-collected"),
    MONTHLY_PREMIUM_COLLECTED("monthly-premium-collected"),
    MONTHLY_DISPLAYNAME_AVAILABLE("monthly-displayname-available"),
    MONTHLY_AVAILABLE_ITEM("monthly-available-item"),
    MONTHLY_UNAVAILABLE_ITEM("monthly-unavailable-item"),
    MONTHLY_AVAILABLE_LORE("monthly-available-lore"),
    MONTHLY_AVAILABLE_PREMIUM_LORE("monthly-available-premium-lore"),
    MONTHLY_DISPLAY_NAME_UNAVAILABLE("monthly-displayname-unavailable"),
    MONTHLY_UNAVAILABLE_LORE("monthly-unavailable-lore"),
    MONTHLY_REWARDS("monthly-rewards"),
    MONTHLY_PREMIUM_REWARDS("monthly-premium-rewards");

    private final String text;

    private static final Map<String, String> messages = new HashMap<>();
    private static final Map<String, List<String>> lists = new HashMap<>();

    Lang(final String text) {
        this.text = text;
    }
    public String getText(){return messages.get(text);}
    public String getTextInUppercase(){return messages.get(text).toUpperCase(Locale.ROOT);}
    public String getColoredText() {
        return applyColor(messages.get(text));
    }

    public boolean getBoolean(){return Boolean.parseBoolean(messages.get(text));}

    public int getInt(){return Integer.parseInt(messages.get(text));}
    public long getLong(){return Long.parseLong(messages.get(text)) * 3600000;}
    public String content(Player p) {
        if (DailyRewards.PAPI){
            return PlaceholderAPI.setPlaceholders(p, applyColor(messages.get(text)));
        } else {
            return applyColor(messages.get(text));
        }
    }

    public List<String> getColoredList(final Player player, String... replacements){
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

    /*public List<String> contentLore(Player p){
        List<String> lore = new ArrayList<>();
        for (String str : lists.get(text)){
            if (DailyRewards.getPlugin(DailyRewards.class).papi){
                lore.add(PlaceholderAPI.setPlaceholders(p, applyColor(str)));
            } else {
                lore.add(applyColor(str));
            }
        }
        return lore;
    }*/

    static {
        reload();
    }

    public static void reload(){
        FileConfiguration cfg = DailyRewards.getPlugin(DailyRewards.class).getConfig();
        for (String key : cfg.getConfigurationSection("config").getKeys(true))
            if (key.endsWith("lore") || key.endsWith("rewards") || key.endsWith("notifications") || key.endsWith("help")){
                lists.put(key, cfg.getStringList("config." + key));
            } else {
                messages.put(key, cfg.getString("config." + key));
            }
    }

    private final Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]){6}>");
    public String applyColor(String message){
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