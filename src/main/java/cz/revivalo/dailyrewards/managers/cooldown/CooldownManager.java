package cz.revivalo.dailyrewards.managers.cooldown;

import cz.revivalo.dailyrewards.configuration.data.DataManager;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
import org.bukkit.entity.Player;

public class CooldownManager {

	public static Cooldown getCooldown(final Player player, final RewardType type) {
		final long remainingTime = DataManager.getLong(player.getUniqueId(), type) - System.currentTimeMillis();
		return new Cooldown(remainingTime);
	}

	public static boolean isRewardAvailable(final Player player, final RewardType rewardType) {
		return (DataManager.getLong(player.getUniqueId(), rewardType) - System.currentTimeMillis()) < 0;
	}

	public static void setCooldown(final Player player, RewardType type) {
		DataManager.setValues(player.getUniqueId(), type.toString().toLowerCase(),
				System.currentTimeMillis() + type.getCooldown());
	}
}
