package eu.athelion.dailyrewards.manager.reward.action.response;

import eu.athelion.dailyrewards.configuration.file.Lang;

public enum ClaimActionResponse implements ActionResponse {
    UNAVAILABLE_REWARD(Lang.UNAVAILABLE_REWARD),
    UNKNOWN(null),
    INSUFFICIENT_PERMISSIONS(Lang.INSUFFICIENT_PERMISSIONS),
    INSUFFICIENT_PLAY_TIME(Lang.INSUFFICIENT_PLAY_TIME),
    LOCATED_IN_DISABLED_WORLD(Lang.LOCATED_IN_RESTRICTED_WORLD),
    NOT_ENOUGH_REQUIRED_INVENTORY_SLOTS(Lang.NOT_ENOUGH_FREE_INVENTORY_SLOTS);

    private final Lang message;

    ClaimActionResponse(Lang message) {
        this.message = message;
    }

    public String getMessage() {
        return message.asColoredString();
    }
}
