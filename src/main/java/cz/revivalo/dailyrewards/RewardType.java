package cz.revivalo.dailyrewards;

import cz.revivalo.dailyrewards.files.Config;

public enum RewardType {
    DAILY(Config.DAILY_COOLDOWN.asLong()),
    WEEKLY(Config.WEEKLY_COOLDOWN.asLong()),
    MONTHLY(Config.MONTHLY_COOLDOWN.asLong()),
    ALL(0);


    private final long cooldown;

    RewardType(long cooldown) {
        this.cooldown = cooldown;
    }

    public String toStringInUppercase(){
        return this.name();
    }

    @Override
    public String toString(){
        return this.name().toLowerCase();
    }

    public long getCooldown() {
        return cooldown;
    }
}
