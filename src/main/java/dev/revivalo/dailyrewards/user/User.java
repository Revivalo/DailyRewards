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
    @Getter private final Map<String, Object> data;

    public User(Player player, Map<String, Object> data) {
        this.player = player;
        this.data = data;
    }

    public Cooldown getCooldownOfReward(RewardType rewardType) {
        return new Cooldown(Long.parseLong(String.valueOf(data.get(rewardType.toString()))));
    }

    public Set<RewardType> getAvailableRewards(){
        Set<RewardType> availableRewards = new HashSet<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (RewardType.findByName(entry.getKey()) == null) continue;
            if (new Cooldown(Long.parseLong(String.valueOf(entry.getValue()))).isClaimable()) availableRewards.add(RewardType.findByName(entry.getKey()));
        }
        return availableRewards;
    }

    public boolean hasEnabledJoinNotification() {
        return 1 == Long.parseLong(String.valueOf(data.get("joinNotification")));
    }

    public void setEnabledJoinNotification(boolean set) {
        data.put("joinNotification", set ? "1" : "0");
    }

    public boolean hasEnabledAutoClaim() {
        return 1 == Long.parseLong(String.valueOf(data.get("autoClaim")));
    }

    public void setEnabledAutoClaim(boolean set) {
        data.put("autoClaim", set ? "1" : "0");
    }

    public void updateCooldowns(Map<String, Object> changes) {
        data.putAll(changes);
    }
}
