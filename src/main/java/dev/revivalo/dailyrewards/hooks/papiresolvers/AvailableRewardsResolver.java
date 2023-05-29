package dev.revivalo.dailyrewards.hooks.papiresolvers;

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
        return Optional.ofNullable(UserHandler.getUser(p.getUniqueId())).map(value -> String.valueOf(value.getAvailableRewards().size())).orElse("Loading...");
    }
}
