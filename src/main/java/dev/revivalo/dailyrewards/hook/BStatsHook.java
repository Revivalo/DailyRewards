package dev.revivalo.dailyrewards.hook;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import org.bstats.bukkit.Metrics;
import org.jetbrains.annotations.Nullable;

public class BStatsHook implements IHook<Void> {

    BStatsHook() {
        hook();
    }

    private void hook() {
        int pluginId = 12070;
        new Metrics(DailyRewardsPlugin.get(), pluginId);
    }

    @Override
    public boolean isOn() {
        return false;
    }

    @Nullable
    @Override
    public Void getApi() {
        return null;
    }
}
