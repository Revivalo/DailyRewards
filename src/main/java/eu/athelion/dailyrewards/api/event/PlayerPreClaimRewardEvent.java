package eu.athelion.dailyrewards.api.event;

import eu.athelion.dailyrewards.manager.reward.RewardType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerPreClaimRewardEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player claimer;
    private final RewardType claimedReward;
    private final List<String> executedCommands;
    private boolean cancelled = false;

    public PlayerPreClaimRewardEvent(Player claimer, RewardType claimedReward, List<String> executedCommands) {
        this.claimer = claimer;
        this.claimedReward = claimedReward;
        this.executedCommands = executedCommands;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @SuppressWarnings("unused")
    public List<String> getExecutedCommands() {return executedCommands;}

    @SuppressWarnings("unused")
    public RewardType getClaimedReward() {
        return claimedReward;
    }

    @SuppressWarnings("unused")
    public Player getClaimer() {
        return claimer;
    }
}
