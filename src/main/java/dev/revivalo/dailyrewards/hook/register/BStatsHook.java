package dev.revivalo.dailyrewards.hook.register;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.hook.Hook;
import org.bstats.bukkit.Metrics;
import org.jetbrains.annotations.Nullable;

public class BStatsHook implements Hook<Void> {

    @Override
    public void register() {
        int pluginId = 12070;
        new Metrics(DailyRewardsPlugin.get(), pluginId);
    }

    @Override
    public boolean isOn() {
        return true;
    }

    @Nullable
    @Override
    public Void getApi() {
        return null;
    }
}
