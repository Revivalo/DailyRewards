package dev.revivalo.dailyrewards.hooks.papiresolvers;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class PAPIRegister extends PlaceholderExpansion {
    private final HashSet<PlaceholderResolver> resolvers = new HashSet<>();
    public PAPIRegister() {
        registerDefaultResolvers();
    }

    private void registerDefaultResolvers() {
        registerResolver(new AvailableRewardsResolver());
    }

    public void registerResolver(PlaceholderResolver resolver) {
        resolvers.add(resolver);
    }

    @Override
    public String getIdentifier() {
        return "dailyrewards";
    }

    @Override
    public String getAuthor() {
        return DailyRewardsPlugin.get().getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return DailyRewardsPlugin.get().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        String resolvedStr = null;

        for (PlaceholderResolver resolver : resolvers) {
            if (resolver.canResolve(identifier)) {
                resolvedStr = resolver.resolve(player, identifier);
                break;
            }
        }

        return resolvedStr;
    }
}
