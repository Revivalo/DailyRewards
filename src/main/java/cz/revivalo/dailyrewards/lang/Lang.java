package cz.revivalo.dailyrewards.lang;

import cz.revivalo.dailyrewards.DailyRewards;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Lang {
    DAILY_COOLDOWN("daily-cooldown"),
    WEEKLY_COOLDOWN("weekly-cooldown"),
    MONTHLY_COOLDOWN("monthly-cooldown"),
    REWARDRESET("reward-reset"),
    UNAVAILABLEPLAYER("unavailable-player"),
    PERMISSIONMSG("permission-msg"),
    REWARDDONTSET("rewards-dont-set"),
    RELOADMSG("reload-msg"),
    MENUTITLE("menu-title"),
    MENUSIZE("menu-size"),
    FILLBACKGROUND("fill-background-enabled"),
    BACKGROUNDITEM("background-item"),
    AUTOMATICALLYACTIVATE("automatically-activate"),
    ENABLEJOINNOTIFICATION("enable-join-notification"),
    ANNOUNCEENABLED("announce-enabled"),
    JOINNOTIFICATIONDELAY("join-notification-delay"),
    JOINHOVERMESSAGE("join-hover-message"),
    JOINNOTIFICATION("join-notifications"),
    COOLDOWNFORMAT("cooldown-format"),
    COOLDOWNMESSAGE("cooldown-message"),
    UNAVAILABLEREWARDSOUND("unavailable-reward-sound"),

    UPDATECHECKER("update-checker"),

    DAILYPLACEHOLDER("daily-placeholder"),
    DAILYPOSITION("daily-position"),
    DAILYSOUND("daily-sound"),
    DAILYTITLE("daily-title"),
    DAILYSUBTITLE("daily-subtitle"),
    DAILYCOLLECTED("daily-collected"),
    DAILYPREMIUMCOLLECTED("daily-premium-collected"),
    DAILYDISPLAYNAMEAVAILABLE("daily-displayname-available"),
    DAILYAVAILABLEITEM("daily-available-item"),
    DAILYUNAVAILABLEITEM("daily-unavailable-item"),
    DAILYAVAILABLELORE("daily-available-lore"),
    DAILYAVAILABLEPREMIUMLORE("daily-available-premium-lore"),
    DAILYDISPLAYNAMEUNAVAILABLE("daily-displayname-unavailable"),
    DAILYUNAVAILABLELORE("daily-unavailable-lore"),
    DAILYREWARDS("daily-rewards"),
    DAILYPREMIUMREWARDS("daily-premium-rewards"),

    WEEKLYPLACEHOLDER("weekly-placeholder"),
    WEEKLYPOSITION("weekly-position"),
    WEEKLYSOUND("weekly-sound"),
    WEEKLYTITLE("weekly-title"),
    WEEKLYSUBTITLE("weekly-subtitle"),
    WEEKLYCOLLECTED("weekly-collected"),
    WEEKLYPREMIUMCOLLECTED("weekly-premium-collected"),
    WEEKLYDISPLAYNAMEAVAILABLE("weekly-displayname-available"),
    WEEKLYAVAILABLEITEM("weekly-available-item"),
    WEEKLYUNAVAILABLEITEM("weekly-unavailable-item"),
    WEEKLYAVAILABLELORE("weekly-available-lore"),
    WEEKLYAVAILABLEPREMIUMLORE("weekly-available-premium-lore"),
    WEEKLYDISPLAYNAMEUNAVAILABLE("weekly-displayname-unavailable"),
    WEEKLYUNAVAILABLELORE("weekly-unavailable-lore"),
    WEEKLYREWARDS("weekly-rewards"),
    WEEKLYPREMIUMREWARDS("weekly-premium-rewards"),

    USEMYSQL("use-mysql"),
    MYSQLIP("mysql-ip"),
    MYSQLDBNAME("mysql-database-name"),
    MYSQLUSERNAME("mysql-username"),
    MYSQLPASSWORD("mysql-password"),

    MONTHLYPLACEHOLDER("monthly-placeholder"),
    MONTHLYPOSITION("monthly-position"),
    MONTHLYSOUND("monthly-sound"),
    MONTHLYTITLE("monthly-title"),
    MONTHLYSUBTITLE("monthly-subtitle"),
    MONTHLYCOLLECTED("monthly-collected"),
    MONTHLYPREMIUMCOLLECTED("monthly-premium-collected"),
    MONTHLYDISPLAYNAMEAVAILABLE("monthly-displayname-available"),
    MONTHLYAVAILABLEITEM("monthly-available-item"),
    MONTHLYUNAVAILABLEITEM("monthly-unavailable-item"),
    MONTHLYAVAILABLELORE("monthly-available-lore"),
    MONTHLYAVAILABLEPREMIUMLORE("monthly-available-premium-lore"),
    MONTHLYDISPLAYNAMEUNAVAILABLE("monthly-displayname-unavailable"),
    MONTHLYUNAVAILABLELORE("monthly-unavailable-lore"),
    MONTHLYREWARDS("monthly-rewards"),
    MONTHLYPREMIUMREWARDS("monthly-premium-rewards");

    private final String text;

    private static final Map<String, String> messages = new HashMap<>();
    private static final Map<String, List<String>> lores = new HashMap<>();

    Lang(final String text) {
        this.text = text;
    }

    public String content() {
        return applyColor(messages.get(text));
    }

    public boolean getBoolean(){return Boolean.parseBoolean(messages.get(text));}

    public int getInt(){return Integer.parseInt(messages.get(text));}
    public long getLong(){return Long.parseLong(messages.get(text)) * 3600000;}
    public String content(Player p) {
        if (DailyRewards.getPlugin(DailyRewards.class).papi){
            return PlaceholderAPI.setPlaceholders(p, applyColor(messages.get(text)));
        } else {
            return applyColor(messages.get(text));
        }
    }

    public List<String> contentLore(Player p){
        List<String> lore = new ArrayList<>();
        for (String str : lores.get(text)){
            if (DailyRewards.getPlugin(DailyRewards.class).papi){
                lore.add(PlaceholderAPI.setPlaceholders(p, applyColor(str)));
            } else {
                lore.add(applyColor(str));
            }
        }
        return lore;
    }

    static {
        reload();
    }

    public static void reload(){
        FileConfiguration cfg = DailyRewards.getPlugin(DailyRewards.class).getConfig();
        for (String key : cfg.getConfigurationSection("config").getKeys(true))
            if (key.endsWith("lore") || key.endsWith("rewards") || key.endsWith("notifications")){
                lores.put(key, cfg.getStringList("config." + key));
            } else {
                messages.put(key, cfg.getString("config." + key));
            }
    }

    private final Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]){6}>");
    public String applyColor(String message){
        if (DailyRewards.isHexSupport) {
            Matcher matcher = hexPattern.matcher(message);
            while (matcher.find()) {
                final ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
                final String before = message.substring(0, matcher.start());
                final String after = message.substring(matcher.end());
                message = before + hexColor + after;
                matcher = hexPattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}