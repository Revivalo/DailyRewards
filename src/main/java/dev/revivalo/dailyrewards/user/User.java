package dev.revivalo.dailyrewards.user;

import dev.revivalo.dailyrewards.managers.cooldown.Cooldown;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class User {
    private final Player player;
    private final Map<RewardType, Cooldown> cooldowns;

    public Cooldown getCooldownOfReward(RewardType rewardType) {
        return cooldowns.get(rewardType);
    }

    public Set<RewardType> getAvailableRewards(){
        Set<RewardType> availableRewards = new HashSet<>();
        for (Map.Entry<RewardType, Cooldown> entry : cooldowns.entrySet()) {
            if (entry.getValue().isClaimable()) availableRewards.add(entry.getKey());
        }
        return availableRewards;
    }

    public void updateCooldowns(Map<RewardType, Long> changes) {
        changes.forEach((key, value) -> cooldowns.put(key, new Cooldown(value)));
    }
}
