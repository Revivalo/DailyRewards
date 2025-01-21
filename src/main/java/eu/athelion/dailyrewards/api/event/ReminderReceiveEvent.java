package eu.athelion.dailyrewards.api.event;

import eu.athelion.dailyrewards.manager.reward.RewardType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ReminderReceiveEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player receiver;
    private final Set<RewardType> availableRewards;
    private boolean cancelled = false;

    public ReminderReceiveEvent(Player receiver, Set<RewardType> availableRewards) {
        this.receiver = receiver;
        this.availableRewards = availableRewards;
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
    public Player getReceiver() {
        return receiver;
    }

    @SuppressWarnings("unused")
    public Set<RewardType> getAvailableRewards() {
        return availableRewards;
    }
}