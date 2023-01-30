package cz.revivalo.dailyrewards.configuration.data;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.configuration.enums.Config;
import cz.revivalo.dailyrewards.managers.cooldown.CooldownManager;
import cz.revivalo.dailyrewards.managers.database.MySQLManager;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;

public class DataManager {

	private static final List<RewardType> rewards = Arrays.asList(RewardType.DAILY, RewardType.WEEKLY, RewardType.MONTHLY);
	@Getter @Setter
	private static boolean usingMysql;

	@SneakyThrows
	public static void setValues(final UUID id, Object... data) {
		Bukkit.getScheduler().runTaskAsynchronously(DailyRewards.getPlugin(), () -> {
			if (isUsingMysql()) {
				try {
					MySQLManager.updateCooldown(id, data);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				return;
			}

			final PlayerData playerData = PlayerData.getConfig(id);
			for (int i = 0; i < data.length; i += 2)
				playerData.set(String.format("rewards.%s", data[i]), data[i + 1]);

			playerData.save();
		});
	}

	public static void createPlayer(final Player player){
		Bukkit.getScheduler().runTask(DailyRewards.getPlugin(), () -> {
			if (isUsingMysql()) MySQLManager.createPlayer(player.getUniqueId().toString());
			else
			if (!PlayerData.exists(player.getUniqueId())){
				final PlayerData playerData = PlayerData.getConfig(player.getUniqueId());
				final long currentTimeInMillis = System.currentTimeMillis();
				if (!Config.DAILY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean()) playerData.set("rewards." + RewardType.DAILY, Config.DAILY_COOLDOWN.asLong() + currentTimeInMillis);
				if (!Config.WEEKLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean()) playerData.set("rewards." + RewardType.WEEKLY, Config.WEEKLY_COOLDOWN.asLong() + currentTimeInMillis);
				if (!Config.MONTHLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean()) playerData.set("rewards." + RewardType.MONTHLY, Config.MONTHLY_COOLDOWN.asLong() + currentTimeInMillis);
				playerData.save();
			}
		});
	}

	public static Collection<RewardType> getAvailableRewards(final Player player) {
		final Collection<RewardType> availableRewards = new HashSet<>();

		rewards.forEach(rewardType -> {
			if (!rewardType.isEnabled()) return;
			if (!CooldownManager.isRewardAvailable(player, rewardType)) return;
			final String rewardName = rewardType.toString().toLowerCase();
			if ((!player.hasPermission(String.format("dailyreward.%s", rewardName))
					&& !player.hasPermission(String.format("dailyreward.%s.premium", rewardName))))
				return;

			availableRewards.add(rewardType);
		});
		return availableRewards;
	}

	public static long getLong(final UUID id, final RewardType type) {
		return isUsingMysql() ? MySQLManager.getRewardsCooldown(id, type) : PlayerData.getConfig(id).getLong("rewards." + type);
	}
}
