package eu.athelion.dailyrewards.api.event;

import eu.athelion.dailyrewards.manager.reward.RewardType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class AutoClaimEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player claimer;
    private final Set<RewardType> claimedRewards;
    private boolean cancelled = false;

    public AutoClaimEvent(Player claimer, Set<RewardType> claimedRewards) {
        this.claimer = claimer;
        this.claimedRewards = claimedRewards;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @SuppressWarnings("unused")
    public Player getClaimer() {
        return claimer;
    }

    @SuppressWarnings("unused")
    public Set<RewardType> getClaimedRewards() {
        return claimedRewards;
    }
}