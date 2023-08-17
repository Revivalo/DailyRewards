package dev.revivalo.dailyrewards.managers.cooldown;

import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.managers.reward.Reward;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CooldownManager {

	public static void setCooldown(final Player player, Reward reward) {
		DataManager.updateValues(player.getUniqueId(),
				null, new HashMap<String, Object>(){{
					put(reward.getRewardName(), System.currentTimeMillis() + reward.getCooldown());
				}}
		);
	}
}
