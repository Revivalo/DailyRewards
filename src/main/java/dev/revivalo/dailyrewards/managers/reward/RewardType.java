package dev.revivalo.dailyrewards.managers.reward;

import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;

import java.util.Arrays;


public enum RewardType {
	DAILY("dailyreward.daily", Lang.DAILY_NAME, Config.DAILY_PLACEHOLDER),
	WEEKLY("dailyreward.weekly", Lang.WEEKLY_NAME, Config.WEEKLY_PLACEHOLDER),
	MONTHLY("dailyreward.monthly", Lang.MONTHLY_NAME, Config.MONTHLY_PLACEHOLDER);

	private final String permission;
	private final Lang name;
	private final Config placeholder;

	RewardType(String permission, Lang name, Config placeholder) {
		this.permission = permission;
		this.name = name;
		this.placeholder = placeholder;
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

	public String getPlaceholder() {
		return placeholder.asString();
	}

	public String getPermission() {
		return permission;
	}
}
