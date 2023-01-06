package cz.revivalo.dailyrewards.managers.reward;

import cz.revivalo.dailyrewards.configuration.enums.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum RewardType {
	DAILY(Config.DAILY_COOLDOWN.asLong()),
	WEEKLY(Config.WEEKLY_COOLDOWN.asLong()),
	MONTHLY(Config.MONTHLY_COOLDOWN.asLong()),
	ALL(0);

	@Getter
	private final long cooldown;

	public static RewardType findByCooldown(long cooldown) {
		return Arrays.stream(RewardType.values())
				.filter(value -> value.getCooldown() == cooldown).findFirst()
				.orElse(null);
	}

	public static RewardType findByName(String name) {
		return Arrays.stream(RewardType.values())
				.filter(value -> value.name().equalsIgnoreCase(name)).findFirst()
				.orElse(null);
	}

	@Override
	public String toString() {
		return this.name();
	}
}
