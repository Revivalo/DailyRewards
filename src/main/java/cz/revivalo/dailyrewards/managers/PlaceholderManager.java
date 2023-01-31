package cz.revivalo.dailyrewards.managers;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.configuration.data.DataManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderManager extends PlaceholderExpansion {

	@Override
	public String getIdentifier() {
		return "dailyrewards";
	}

	@Override
	public String getAuthor() {
		return DailyRewards.getPlugin().getDescription().getAuthors().toString();
	}

	@Override
	public String getVersion() {
		return DailyRewards.getPlugin().getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(final Player player, String identifier) {
		return player == null
				? ""
				: identifier.equalsIgnoreCase("available")
					? String.valueOf(DataManager.getAvailableRewards(player).size())
					: null;
	}
}