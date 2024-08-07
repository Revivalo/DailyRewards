package dev.revivalo.dailyrewards.manager.reward;

import dev.revivalo.dailyrewards.configuration.file.Config;
import dev.revivalo.dailyrewards.configuration.file.Lang;

import java.util.Arrays;


public enum RewardType {
	DAILY("dailyreward.daily", Lang.DAILY_NAME, Config.DAILY_PLACEHOLDER, Config.DAILY_ENABLED),
	WEEKLY("dailyreward.weekly", Lang.WEEKLY_NAME, Config.WEEKLY_PLACEHOLDER, Config.WEEKLY_ENABLED),
	MONTHLY("dailyreward.monthly", Lang.MONTHLY_NAME, Config.MONTHLY_PLACEHOLDER, Config.MONTHLY_ENABLED);

	private final String permission;
	private final Lang name;
	private final Config placeholder;
	private final Config enabled;

	RewardType(String permission, Lang name, Config placeholder, Config enabled) {
		this.permission = permission;
		this.name = name;
		this.placeholder = placeholder;
		this.enabled = enabled;
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

	public boolean isEnabled() {return enabled.asBoolean();}
}
