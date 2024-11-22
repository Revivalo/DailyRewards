package dev.revivalo.dailyrewards.manager.cooldown;

import dev.revivalo.dailyrewards.data.DataManager;
import dev.revivalo.dailyrewards.manager.reward.Reward;
import dev.revivalo.dailyrewards.user.User;

import java.util.HashMap;

public class CooldownManager {

	public static void setCooldown(final User user, Reward reward) {
		DataManager.updateValues(
				user.getPlayer().getUniqueId(),
				user,
				new HashMap<String, Object>() {{
					put(reward.getName(), System.currentTimeMillis() + reward.getCooldown());
				}}
		);
	}
}
