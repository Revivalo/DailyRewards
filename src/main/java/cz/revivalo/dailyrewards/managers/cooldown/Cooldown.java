package cz.revivalo.dailyrewards.managers.cooldown;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(setterPrefix = "set")
@RequiredArgsConstructor
public class Cooldown {
	private final String format;
	private final Long timeLeft;

	public boolean isClaimable() {
		return timeLeft <= 0;
	}
}
