package dev.revivalo.dailyrewards.managers.reward.actions.responses;

public interface ActionResponse {
    ActionResponse PROCEEDED = ActionResponseType.PROCEEDED;
    ActionResponse NO_PERMISSION = ActionResponseType.NO_PERMISSION;
    ActionResponse UNAVAILABLE_PLAYER = ActionResponseType.UNAVAILABLE_PLAYER;

    static boolean isProceeded(ActionResponse response) {
        return response == PROCEEDED;
    }

    enum ActionResponseType implements ActionResponse {
        PROCEEDED,
        NO_PERMISSION,
        UNAVAILABLE_PLAYER
    }
}
