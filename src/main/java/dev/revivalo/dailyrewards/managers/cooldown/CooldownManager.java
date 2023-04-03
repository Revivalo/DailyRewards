package dev.revivalo.dailyrewards.managers.cooldown;

import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CooldownManager {

	public static void setCooldown(final Player player, RewardType type) {
		DataManager.setValues(player.getUniqueId(),
				new HashMap<RewardType, Long>(){{
					put(type, System.currentTimeMillis() + type.getCooldown());
				}}
		);
	}
}
