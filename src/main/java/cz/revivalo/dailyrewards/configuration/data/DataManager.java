package cz.revivalo.dailyrewards.configuration.data;

import cz.revivalo.dailyrewards.managers.cooldown.CooldownManager;
import cz.revivalo.dailyrewards.managers.database.MySQLManager;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.util.*;

public class DataManager {

	private static final List<RewardType> dailyRewards = Arrays.asList(RewardType.DAILY, RewardType.WEEKLY, RewardType.MONTHLY);
	@Getter @Setter
	private static boolean usingMysql;

	@SneakyThrows
	public static void setValues(final UUID id, Object... data) {
		if (DataManager.isUsingMysql()) {
			MySQLManager.updateCooldown(id, data);
			return;
		}

		final PlayerData playerData = PlayerData.getConfig(id);
		for (int i = 0; i < data.length; i += 2)
			playerData.set(String.format("rewards.%s", data[i]), data[i + 1]);

		playerData.save();
	}

	public static Collection<RewardType> getAvailableRewards(final Player player) {
		final Collection<RewardType> availableRewards = new HashSet<>();

		dailyRewards.forEach(rewardType -> {
			if (!CooldownManager.isRewardAvailable(player, RewardType.DAILY)) return;
			final String rewardName = rewardType.toString().toLowerCase();
			if ((!player.hasPermission(
					String.format("dailyreward.%s", rewardName)) && !player.hasPermission(
					String.format("dailyreward.%s.premium", rewardName))))
				return;

			availableRewards.add(rewardType);
		});
		return availableRewards;
	}

	public static long getLong(final UUID id, final RewardType type) {
		return DataManager.isUsingMysql() ? MySQLManager.getRewardsCooldown(id, type) : PlayerData.getConfig(id).getLong("rewards." + type);
	}
}
