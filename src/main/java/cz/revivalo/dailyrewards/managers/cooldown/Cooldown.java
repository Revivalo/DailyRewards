package cz.revivalo.dailyrewards.managers.cooldown;

import cz.revivalo.dailyrewards.configuration.enums.Config;

public class Cooldown {
	private Long timeLeft;

	public Cooldown(Long timeLeft) {
		this.timeLeft = timeLeft;
	}

	public boolean isClaimable(){
		return timeLeft < 0;
	}

	public Long getTimeLeft() {
		return timeLeft;
	}

	public String getFormat(String format) {
		return Config.formatTime(format, timeLeft);
	}

	public void setTimeLeft(long timeLeft){
		this.timeLeft = timeLeft;
	}
}
