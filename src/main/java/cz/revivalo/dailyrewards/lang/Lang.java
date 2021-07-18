package cz.revivalo.dailyrewards.lang;

import cz.revivalo.dailyrewards.DailyRewards;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Lang {
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

    private static Map<String, String> messages = new HashMap<>();
    private static Map<String, List<String>> lores = new HashMap<>();

    Lang(final String text) {
        this.text = text;
    }

    public String content() {
        return ChatColor.translateAlternateColorCodes('&', messages.get(text));
    }

    public String content(Player p) {
        if (DailyRewards.getPlugin(DailyRewards.class).papi){
            return PlaceholderAPI.setPlaceholders(p, ChatColor.translateAlternateColorCodes('&', messages.get(text)));
        } else {
            return ChatColor.translateAlternateColorCodes('&', messages.get(text));
        }
    }

    public List<String> contentLore(Player p){
        List<String> lore = new ArrayList<>();
        for (String str : lores.get(text)){
            if (DailyRewards.getPlugin(DailyRewards.class).papi){
                lore.add(PlaceholderAPI.setPlaceholders(p, ChatColor.translateAlternateColorCodes('&', str)));
            } else {
                lore.add(ChatColor.translateAlternateColorCodes('&', str));
            }
        }
        return lore;
    }

    static {
        FileConfiguration cfg = DailyRewards.getPlugin(DailyRewards.class).getConfig();
        for (String key : cfg.getConfigurationSection("config").getKeys(true))
            if (key.endsWith("lore") || key.endsWith("rewards") || key.endsWith("notifications")){
                lores.put(key, cfg.getStringList("config." + key));
            } else {
                messages.put(key, cfg.getString("config." + key));
            }
    }

    public static void reload(){
        FileConfiguration cfg = DailyRewards.getPlugin(DailyRewards.class).getConfig();
        for (String key : cfg.getConfigurationSection("config").getKeys(true))
            if (key.endsWith("lore") || key.endsWith("rewards")){
                lores.put(key, cfg.getStringList("config." + key));
            } else {
                messages.put(key, cfg.getString("config." + key));
            }
    }
}