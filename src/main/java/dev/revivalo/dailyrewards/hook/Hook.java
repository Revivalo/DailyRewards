package dev.revivalo.dailyrewards.hook;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public interface Hook<T> {
    default void preRegister() {
        register();
        if (isOn()) {
            DailyRewardsPlugin.get().getLogger().log(Level.INFO, this.getClass().getSimpleName() + " has been registered.");
        }
    }

    default boolean isPluginEnabled(String name) {
        return DailyRewardsPlugin.get().isPluginLoaded(name);
    }

    default Plugin getPlugin(String name) {
        return DailyRewardsPlugin.get().getPlugin(name);
    }


    void register();
    boolean isOn();

    @Nullable
    T getApi();
}
