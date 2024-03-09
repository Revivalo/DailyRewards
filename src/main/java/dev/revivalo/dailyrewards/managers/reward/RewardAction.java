package dev.revivalo.dailyrewards.managers.reward;

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

    public String getStatement() {
        return this.statement;
    }

    public ActionType getActionType() {
        return this.actionType;
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