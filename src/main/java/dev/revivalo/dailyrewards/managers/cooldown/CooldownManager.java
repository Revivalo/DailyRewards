package dev.revivalo.dailyrewards.managers.cooldown;

import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.managers.reward.Reward;
import dev.revivalo.dailyrewards.user.User;

import java.util.HashMap;

public class CooldownManager {

	public static void setCooldown(final User user, Reward reward) {
		DataManager.updateValues(
				user.getPlayer().getUniqueId(),
				user,
				new HashMap<String, Object>() {{
					put(reward.getRewardName(), System.currentTimeMillis() + reward.getCooldown());
				}}
		);
	}
}
