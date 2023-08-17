package dev.revivalo.dailyrewards.managers.reward;

import java.util.Arrays;


public enum RewardType {
	DAILY(),
	WEEKLY(),
	MONTHLY();


	public static RewardType findByName(String name) {
		return Arrays.stream(RewardType.values())
				.filter(value -> value.name().equalsIgnoreCase(name)).findFirst()
				.orElse(null);
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}

}
