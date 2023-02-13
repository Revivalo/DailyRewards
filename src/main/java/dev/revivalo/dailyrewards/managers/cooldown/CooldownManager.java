package dev.revivalo.dailyrewards.managers.cooldown;

import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CooldownManager {

	public static Cooldown getCooldown(final Player player, final RewardType type) {
		final long remainingTime = DataManager.getLong(player.getUniqueId(), type) - System.currentTimeMillis();
		return new Cooldown(remainingTime);
	}

	public static boolean isRewardAvailable(final Player player, final RewardType rewardType) {
		return (DataManager.getLong(player.getUniqueId(), rewardType) - System.currentTimeMillis()) <= 0;
	}

	public static void setCooldown(final Player player, RewardType type) {
		DataManager.setValues(player.getUniqueId(),
				new HashMap<String, Object>(){{
					put(type.toString().toLowerCase(), System.currentTimeMillis() + type.getCooldown());
				}}
		);
	}
}
