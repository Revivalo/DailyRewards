package cz.revivalo.dailyrewards.managers;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.files.DataManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class Placeholder extends PlaceholderExpansion {
    private final DailyRewards plugin;
    public Placeholder(final DailyRewards plugin) {
        this.plugin = plugin;
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
    public String onPlaceholderRequest(final Player player, String identifier) {
        if (player == null) {
            return "";
        }
        if (identifier.equalsIgnoreCase("available")){
            return String.valueOf(DataManager.getAvailableRewards(player).size());
        }
        return null;
    }
}