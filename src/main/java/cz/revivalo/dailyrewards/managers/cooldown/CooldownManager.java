package cz.revivalo.dailyrewards.managers.cooldown;

import cz.revivalo.dailyrewards.configuration.data.DataManager;
import cz.revivalo.dailyrewards.configuration.enums.Config;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class CooldownManager {

	public static Cooldown getCooldown(final Player player, final RewardType type) {
		final long remainingTime = DataManager.getLong(player.getUniqueId(), type) - System.currentTimeMillis();
		switch (type) {
			case WEEKLY:
			case MONTHLY:
				return Cooldown.builder()
						.setFormat(String.format(Config.COOL_DOWN_FORMAT.asString()
										.replace("%days%", "%02d")
										.replace("%hours%", "%02d"),
								TimeUnit.MILLISECONDS.toDays(remainingTime),
								TimeUnit.MILLISECONDS.toHours(remainingTime) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(remainingTime))))
						.setTimeLeft(remainingTime)
						.build();
			case DAILY:
				return Cooldown.builder()
						.setFormat(String.format("%02d:%02d:%02d",
								TimeUnit.MILLISECONDS.toHours(remainingTime),
								TimeUnit.MILLISECONDS.toMinutes(remainingTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime)),
								TimeUnit.MILLISECONDS.toSeconds(remainingTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime))))
						.setTimeLeft(remainingTime)
						.build();

			default:
				return Cooldown.builder()
						.setFormat("An Error Occurred")
						.setTimeLeft(0L)
						.build();
		}
	}

	public static boolean isRewardAvailable(final Player player, final RewardType rewardType) {
		return (DataManager.getLong(player.getUniqueId(), rewardType) - System.currentTimeMillis()) < 0;
	}

	public static void setCooldown(final Player player, RewardType type) {
		DataManager.setValues(player.getUniqueId(), type.toString().toLowerCase(),
				System.currentTimeMillis() + type.getCooldown());
	}
}
