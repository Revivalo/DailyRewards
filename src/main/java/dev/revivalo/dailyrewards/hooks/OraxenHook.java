package dev.revivalo.dailyrewards.hooks;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import org.jetbrains.annotations.Nullable;

public class OraxenHook implements Hook<Void> {

    private final boolean isHooked;
    OraxenHook(){
        isHooked = DailyRewardsPlugin.get().getPluginManager().getPlugin("Oraxen") != null;
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Override
    public @Nullable Void getApi() {
        return null;
    }
}
