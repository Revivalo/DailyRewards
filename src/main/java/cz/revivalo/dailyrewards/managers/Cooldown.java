package cz.revivalo.dailyrewards.managers;

public class Cooldown {
    private final String format;
    private final Long timeLeft;

    public Cooldown(String format, Long timeLeft) {
        this.format = format;
        this.timeLeft = timeLeft;
    }

    public boolean isClaimable(){
        return timeLeft < 0;
    }

    public Long getTimeLeft() {
        return timeLeft;
    }

    public String getFormat() {
        return format;
    }
}
