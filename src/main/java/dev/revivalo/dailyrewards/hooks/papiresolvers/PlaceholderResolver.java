package dev.revivalo.dailyrewards.hooks.papiresolvers;

import org.bukkit.entity.Player;

public interface PlaceholderResolver {
    boolean canResolve(String rawPlaceholder);
    String resolve(Player p, String rawPlaceholder);
}
