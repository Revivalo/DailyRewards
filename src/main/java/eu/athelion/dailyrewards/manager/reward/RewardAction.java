package eu.athelion.dailyrewards.manager.reward;

import lombok.Getter;

@Getter
public class RewardAction {
    private final String statement;
    private final ActionType actionType;

    RewardAction(String statement, ActionType actionType) {
        this.statement = statement;
        this.actionType = actionType;
    }

    public static RewardActionBuilder builder() {
        return new RewardActionBuilder();
    }

    public static class RewardActionBuilder {
        private String executedCommand;
        private ActionType actionType;

        public RewardActionBuilder setExecutedCommand(String executedCommand) {
            this.executedCommand = executedCommand;
            return this;
        }

        public RewardActionBuilder setActionType(ActionType actionType) {
            this.actionType = actionType;
            return this;
        }

        public RewardAction build() {
            return new RewardAction(executedCommand, actionType);
        }
    }
}