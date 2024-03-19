package dev.revivalo.dailyrewards.managers.reward.actions.responses;

public enum ClaimActionResponse implements ActionResponse {
    UNAVAILABLE_REWARD,
    UNKNOWN,
    INSUFFICIENT_PERMISSIONS,
    NOT_ENOUGH_REQUIRED_INVENTORY_SLOTS
}
