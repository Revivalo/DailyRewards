package dev.revivalo.dailyrewards.managers.reward;

import dev.revivalo.dailyrewards.configuration.enums.Lang;

import java.util.Arrays;


public enum RewardType {
	DAILY("dailyreward.daily", Lang.DAILY_NAME),
	WEEKLY("dailyreward.weekly", Lang.WEEKLY_NAME),
	MONTHLY("dailyreward.monthly", Lang.MONTHLY_NAME);

	private final String permission;
	private final Lang name;

	RewardType(String permission, Lang name) {
		this.permission = permission;
		this.name = name;
	}

	public static RewardType findByName(String name) {
		return Arrays.stream(RewardType.values())
				.filter(value -> value.name().equalsIgnoreCase(name)).findFirst()
				.orElse(null);
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}

	public String getName() {
		return name.asColoredString();
	}

	public String getPermission() {
		return permission;
	}
}
