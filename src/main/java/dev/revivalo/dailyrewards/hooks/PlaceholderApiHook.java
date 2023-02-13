package dev.revivalo.dailyrewards.hooks;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.hooks.papiresolvers.AvailableRewardsResolver;
import dev.revivalo.dailyrewards.hooks.papiresolvers.PlaceholderResolver;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class PlaceholderApiHook extends PlaceholderExpansion implements Hook<PlaceholderExpansion> {
    private final HashSet<PlaceholderResolver> resolvers = new HashSet<>();
    private boolean isHooked = false;

    PlaceholderApiHook() {
        tryToHook();
    }

    private void tryToHook() {
        this.register();
        isHooked = true;
        registerDefaultResolvers();
    }

    private void registerDefaultResolvers() {
        registerResolver(new AvailableRewardsResolver());
    }

    public void registerResolver(PlaceholderResolver resolver) {
        resolvers.add(resolver);
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Nullable
    @Override
    public PlaceholderExpansion getApi() {
        return isHooked ? this : null;
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
