package dev.revivalo.dailyrewards.user;

import dev.revivalo.dailyrewards.managers.cooldown.Cooldown;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class User {
    @Getter private final Player player;
    @Getter private final Map<String, Long> data;

    public User(Player player, Map<String, Long> data) {
        this.player = player;
        this.data = data;
    }

    public Cooldown getCooldownOfReward(RewardType rewardType) {
        return new Cooldown(data.get(rewardType.toString()));
    }

    public Set<RewardType> getAvailableRewards(){
        Set<RewardType> availableRewards = new HashSet<>();
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            if (RewardType.findByName(entry.getKey()) == null) continue;
            if (new Cooldown(entry.getValue()).isClaimable()) availableRewards.add(RewardType.findByName(entry.getKey()));
        }
        return availableRewards;
    }

    public boolean isEnabledJoinNotification() {
        return 1 == data.get("join-notification");
    }

    public void setEnabledJoinNotification(boolean set) {
        data.put("join-notification", set ? 1L : 0);
    }

    public boolean isEnabledAutoClaim() {
        return 1 == data.get("auto-claim");
    }

    public void setEnabledAutoClaim(boolean set) {
        data.put("auto-claim", set ? 1L : 0);
    }

    public void updateCooldowns(Map<String, Long> changes) {
        data.putAll(changes);
    }
}
