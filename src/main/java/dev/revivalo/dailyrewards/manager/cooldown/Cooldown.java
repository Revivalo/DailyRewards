package dev.revivalo.dailyrewards.manager.cooldown;

import dev.revivalo.dailyrewards.util.TextUtil;

import java.util.concurrent.atomic.AtomicLong;

public class Cooldown {
	private final AtomicLong timeLeftInMillis;

	public Cooldown(long timeLeftInMillis) {
		this.timeLeftInMillis = new AtomicLong(timeLeftInMillis);
	}

	public boolean isClaimable() {
		return getTimeLeftInMillis() <= 0;
	}

	public long getTimeLeftInMillis() {
		return timeLeftInMillis.get() - System.currentTimeMillis();
	}

	public String getFormat(String format) {
		return TextUtil.formatTime(format, getTimeLeftInMillis());
	}
}
