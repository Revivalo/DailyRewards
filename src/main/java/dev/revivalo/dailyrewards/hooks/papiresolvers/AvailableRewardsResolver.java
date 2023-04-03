package dev.revivalo.dailyrewards.hooks.papiresolvers;

import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import org.bukkit.entity.Player;

import java.util.Optional;

public class AvailableRewardsResolver implements PlaceholderResolver {
    @Override
    public boolean canResolve(String rawPlaceholder) {
        return rawPlaceholder.startsWith("available");
    }

    @Override
    public String resolve(Player p, String rawPlaceholder) {
        Optional<User> user = UserHandler.getUser(p.getUniqueId());
        return user.map(value -> String.valueOf(value.getAvailableRewards().size())).orElse("Loading");
    }
}
