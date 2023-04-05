package dev.revivalo.dailyrewards.configuration.data;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.managers.database.MySQLManager;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.user.UserHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

	@Getter @Setter
	private static boolean usingMysql;

	@SneakyThrows
	public static void setValues(final UUID id, Map<RewardType, Long> data) {
			if (isUsingMysql()) {
				try {
					MySQLManager.updateCooldown(id, data);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			} else {

				final PlayerData playerData = PlayerData.getConfig(id);
				final ConfigurationSection rewardsSection = playerData.getConfigurationSection("rewards");

				for (Map.Entry<RewardType, Long> entry : data.entrySet())
					rewardsSection.set(entry.getKey().toString(), entry.getValue());

				playerData.save();
			}

			UserHandler.getUser(id).updateCooldowns(data);
	}

	public static void createPlayer(final Player player){
			if (isUsingMysql()) MySQLManager.createPlayer(player.getUniqueId().toString());
			else if (!PlayerData.exists(player.getUniqueId())){
				final PlayerData playerData = PlayerData.getConfig(player.getUniqueId());
				final ConfigurationSection rewardsSection = playerData.createSection("rewards");

				final long currentTimeInMillis = System.currentTimeMillis();
				if (!Config.DAILY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean()) rewardsSection.set(RewardType.DAILY.toString(), Config.DAILY_COOLDOWN.asLong() + currentTimeInMillis);
				if (!Config.WEEKLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean()) rewardsSection.set(RewardType.WEEKLY.toString(), Config.WEEKLY_COOLDOWN.asLong() + currentTimeInMillis);
				if (!Config.MONTHLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean()) rewardsSection.set(RewardType.MONTHLY.toString(), Config.MONTHLY_COOLDOWN.asLong() + currentTimeInMillis);
				playerData.save();
			}
	}

	public static Map<RewardType, Long> getPlayersCooldowns(Player player){
		Map<RewardType, Long> cooldowns = new HashMap<>();
		if (isUsingMysql()) cooldowns = MySQLManager.getRewardsCooldown(player.getUniqueId());
		else {
			PlayerData playerData = PlayerData.getConfig(player.getUniqueId());
			for (String key : playerData.getConfigurationSection("rewards").getKeys(false)){
				cooldowns.put(RewardType.findByName(key), playerData.getLong("rewards." + key));
			}
		}
		return cooldowns;
	}


	public static void loadPlayerDataAsync(final Player player, final FindOneCallback callback) {
		Bukkit.getScheduler().runTaskAsynchronously(DailyRewardsPlugin.get(), () -> {
			createPlayer(player);
			final Map<RewardType, Long> result = getPlayersCooldowns(player);
			Bukkit.getScheduler().runTask(DailyRewardsPlugin.get(), () -> {
				callback.onQueryDone(result);
			});
		});
	}
}