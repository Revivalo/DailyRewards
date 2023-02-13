package dev.revivalo.dailyrewards.user;

import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class User {
    private final Player player;
    private short availableRewards;

    public User(Player player, short availableRewards){
        this.player = player;
        this.availableRewards = availableRewards;
    }
}
