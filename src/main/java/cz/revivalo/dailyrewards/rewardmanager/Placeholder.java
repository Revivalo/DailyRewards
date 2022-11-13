package cz.revivalo.dailyrewards.rewardmanager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import cz.revivalo.dailyrewards.DailyRewards;

public class Placeholder extends PlaceholderExpansion {
    private final DailyRewards plugin;
    private final Cooldowns cooldowns;
    public Placeholder(final DailyRewards plugin) {
        this.plugin = plugin;
        cooldowns = plugin.getCooldowns();
    }
    @Override
    public String getIdentifier() {
        return "dailyrewards";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
    @Override
    public boolean canRegister() {
        return true;
    }
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        if (identifier.equalsIgnoreCase("available")){
            int available = 0;
            for (int i = 0; i <= 3; i++) {
                if (i == 0) {
                    if (cooldowns.isRewardAvailable(player, "daily") && (player.hasPermission("dailyreward.daily") || player.hasPermission("dailyreward.daily.premium"))) ++available;
                } else if (i == 1) {
                    if (cooldowns.isRewardAvailable(player, "weekly") && (player.hasPermission("dailyreward.weekly") || player.hasPermission("dailyreward.weekly.premium"))) ++available;
                } else if (i == 2) {
                    if (cooldowns.isRewardAvailable(player, "monthly") && (player.hasPermission("dailyreward.monthly") || player.hasPermission("dailyreward.monthly.premium"))) ++available;
                }
            }
            return String.valueOf(available);
        }
        return null;
    }
}