package dev.revivalo.dailyrewards.hooks;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook implements Hook<Void>{
    private final boolean isHooked;
    ItemsAdderHook(){
        isHooked = hook();
    }

    private boolean hook(){
        return DailyRewardsPlugin.get().getPluginManager().getPlugin("ItemsAdder") != null;
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
