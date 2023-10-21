package dev.revivalo.dailyrewards.configuration.data;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.managers.database.MySQLManager;
import dev.revivalo.dailyrewards.managers.reward.Reward;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DataManager {

	private static boolean usingMysql;

	public static void updateValues(final UUID id, User user, Map<String, Object> data) {
		if (isUsingMysql()) {
			MySQLManager.updatePlayer(id, data);
		} else {
			final PlayerData playerData = PlayerData.getConfig(id);
			final ConfigurationSection rewardsSection = playerData.getConfigurationSection("rewards");

			for (Map.Entry<String, Object> entry : data.entrySet())
				rewardsSection.set(entry.getKey(), entry.getValue());

			playerData.save();
		}

		if (Bukkit.getOfflinePlayer(id).isOnline()) {
			Optional.ofNullable(user).orElse(UserHandler.getUser(id)).updateCooldowns(data);
		}
	}

	public static void initiatePlayer(final Player player){
		if (isUsingMysql()) MySQLManager.createPlayer(player.getUniqueId().toString());
		else {
			final PlayerData playerData = PlayerData.getConfig(player.getUniqueId());
			if (!playerData.isConfigurationSection("rewards")) {
				playerData.createSection("rewards");
			}

			final long currentTimeInMillis = System.currentTimeMillis();

			if (!playerData.getConfigurationSection("rewards").isSet(RewardType.DAILY.toString())) playerData.getConfigurationSection("rewards").set(RewardType.DAILY.toString(), Config.DAILY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean() ? 0 : currentTimeInMillis + Config.DAILY_COOLDOWN.asLong() * 60 * 60 * 1000);
			if (!playerData.getConfigurationSection("rewards").isSet(RewardType.WEEKLY.toString())) playerData.getConfigurationSection("rewards").set(RewardType.WEEKLY.toString(), Config.WEEKLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean() ? 0 : currentTimeInMillis + Config.WEEKLY_COOLDOWN.asLong() * 60 * 60 * 1000);
			if (!playerData.getConfigurationSection("rewards").isSet(RewardType.MONTHLY.toString())) playerData.getConfigurationSection("rewards").set(RewardType.MONTHLY.toString(), Config.MONTHLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean() ? 0 : currentTimeInMillis + Config.MONTHLY_COOLDOWN.asLong() * 60 * 60 * 1000);
			if (!playerData.getConfigurationSection("rewards").isSet("autoClaim")) playerData.getConfigurationSection("rewards").set("autoClaim", Config.AUTO_CLAIM_REWARDS_ON_JOIN_BY_DEFAULT.asBoolean() ? 1 : 0);
			if (!playerData.getConfigurationSection("rewards").isSet("joinNotification")) playerData.getConfigurationSection("rewards").set("joinNotification", Config.JOIN_NOTIFICATION_BY_DEFAULT.asBoolean() ? 1 : 0);

			playerData.save();
		}
	}

	public static Map<String, Object> getPlayerData(Player player){
		Map<String, Object> data = new HashMap<>();
		if (isUsingMysql()) data = MySQLManager.getRewardsCooldown(player.getUniqueId());
		else {
			PlayerData playerData = PlayerData.getConfig(player.getUniqueId());
			for (Reward reward : DailyRewardsPlugin.getRewardManager().getRewards()) {
				final RewardType rewardType = reward.getRewardType();
				data.put(rewardType.toString(), playerData.getString("rewards." + rewardType));
			}
			data.put("autoClaim", playerData.getString("rewards.autoClaim"));
			data.put("joinNotification", playerData.getString("rewards.joinNotification"));
		}
		return data;
	}

	public static void loadPlayerDataAsync(final Player player, final FindOneCallback callback) {
		initiatePlayer(player);
		Bukkit.getScheduler().runTaskAsynchronously(DailyRewardsPlugin.get(), () -> {
			final Map<String, Object> result = getPlayerData(player);
			Bukkit.getScheduler().runTask(DailyRewardsPlugin.get(), () -> callback.onQueryDone(result));
		});
	}

	public static boolean isUsingMysql() {
		return usingMysql;
	}

	public static void setUsingMysql(boolean usingMysql) {
		DataManager.usingMysql = usingMysql;
	}
}