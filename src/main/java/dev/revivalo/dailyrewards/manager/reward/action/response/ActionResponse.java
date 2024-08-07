package dev.revivalo.dailyrewards.manager.reward.action.response;

public interface ActionResponse {

    static boolean isProceeded(ActionResponse response) {
        return response == Type.PROCEEDED;
    }

    enum Type implements ActionResponse {
        PROCEEDED,
        NO_PERMISSION,
        UNAVAILABLE_PLAYER,
        UNAVAILABLE_REWARD
    }
}
