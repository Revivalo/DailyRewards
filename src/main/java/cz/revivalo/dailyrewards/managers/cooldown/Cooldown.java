package cz.revivalo.dailyrewards.managers.cooldown;

import cz.revivalo.dailyrewards.configuration.enums.Config;

import java.util.concurrent.atomic.AtomicLong;

public class Cooldown {
	private final AtomicLong timeLeftInMillis;

	public Cooldown(long timeLeftInMillis) {
		this.timeLeftInMillis = new AtomicLong(timeLeftInMillis);
	}

	public boolean isClaimable(){
		return getTimeLeftInMillis() <= 0;
	}

	public Long getTimeLeftInMillis() {
		return timeLeftInMillis.get();
	}

	public void reduce(int millis){
		timeLeftInMillis.addAndGet(-millis);
	}

	public String getFormat(String format) {
		return Config.formatTime(format, getTimeLeftInMillis());
	}
}
