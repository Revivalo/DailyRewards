package dev.revivalo.dailyrewards.managers;

public enum Setting {
    JOIN_NOTIFICATION("joinNotification"),
    AUTO_CLAIM("autoClaim");

    private final String name;
    Setting(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
