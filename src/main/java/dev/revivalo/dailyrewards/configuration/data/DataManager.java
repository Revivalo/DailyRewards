package dev.revivalo.dailyrewards.configuration.data;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.managers.database.MySQLManager;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DataManager {

	@Getter @Setter
	private static boolean usingMysql;

	@SneakyThrows
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
			//final ConfigurationSection rewardsSection = playerData.createSection("rewards");

			//ConfigurationSection section = Optional.ofNullable(playerData.getConfigurationSection("rewards")).orElse(playerData.createSection("rewards"));

			final long currentTimeInMillis = System.currentTimeMillis();

			if (!playerData.getConfigurationSection("rewards").isSet(RewardType.DAILY.toString())) playerData.getConfigurationSection("rewards").set(RewardType.DAILY.toString(), Config.DAILY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean() ? 0 : currentTimeInMillis + RewardType.DAILY.getCooldown());
			if (!playerData.getConfigurationSection("rewards").isSet(RewardType.WEEKLY.toString())) playerData.getConfigurationSection("rewards").set(RewardType.WEEKLY.toString(), Config.WEEKLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean() ? 0 : currentTimeInMillis + RewardType.WEEKLY.getCooldown());
			if (!playerData.getConfigurationSection("rewards").isSet(RewardType.MONTHLY.toString())) playerData.getConfigurationSection("rewards").set(RewardType.MONTHLY.toString(), Config.MONTHLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean() ? 0 : currentTimeInMillis + RewardType.MONTHLY.getCooldown());
			if (!playerData.getConfigurationSection("rewards").isSet("auto-claim")) playerData.getConfigurationSection("rewards").set("auto-claim", Config.AUTO_CLAIM_REWARDS_ON_JOIN_BY_DEFAULT.asBoolean() ? 1 : 0);
			if (!playerData.getConfigurationSection("rewards").isSet("join-notification")) playerData.getConfigurationSection("rewards").set("join-notification", Config.JOIN_NOTIFICATION_BY_DEFAULT.asBoolean() ? 1 : 0);

//			if (playerData.isSet("rewards.auto-claim")) playerData.set("auto-claim", Config.AUTO_CLAIM_REWARDS_ON_JOIN_BY_DEFAULT.asBoolean());
//			if (playerData.isSet("join-notification")) playerData.set("join-notification", Config.JOIN_NOTIFICATION_BY_DEFAULT.asBoolean());

			playerData.save();
		}
	}

	public static Map<String, Object> getPlayerData(Player player){
		Map<String, Object> data = new HashMap<>();
		if (isUsingMysql()) data = MySQLManager.getRewardsCooldown(player.getUniqueId());
		else {
			PlayerData playerData = PlayerData.getConfig(player.getUniqueId());
			for (RewardType rewardType : RewardType.values()) {
				data.put(rewardType.toString(), playerData.getString("rewards." + rewardType));
			}
			data.put("autoClaim", playerData.getString("rewards.auto-claim"));
			data.put("joinNotification", playerData.getString("rewards.join-notification"));
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
}