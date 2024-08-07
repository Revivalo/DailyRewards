package dev.revivalo.dailyrewards.api.event;

import dev.revivalo.dailyrewards.manager.reward.RewardType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerClaimRewardEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player claimer;
    private final RewardType claimedReward;

    public PlayerClaimRewardEvent(Player claimer, RewardType claimedReward) {
        this.claimer = claimer;
        this.claimedReward = claimedReward;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @SuppressWarnings("unused")
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }


    @SuppressWarnings("unused")
    public Player getClaimer() {
        return claimer;
    }

    @SuppressWarnings("unused")
    public RewardType getClaimedReward() {
        return claimedReward;
    }
}
