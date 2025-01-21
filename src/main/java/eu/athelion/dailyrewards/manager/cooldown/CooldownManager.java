package eu.athelion.dailyrewards.manager.cooldown;

import eu.athelion.dailyrewards.data.DataManager;
import eu.athelion.dailyrewards.manager.reward.Reward;
import eu.athelion.dailyrewards.user.User;

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
