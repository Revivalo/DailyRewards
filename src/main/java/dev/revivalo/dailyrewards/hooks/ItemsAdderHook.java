package dev.revivalo.dailyrewards.hooks;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.listeners.ItemsAdderLoadDataListener;
import dev.revivalo.dailyrewards.utils.VersionUtils;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook implements Hook<Void>{
    private final boolean isHooked;
    ItemsAdderHook(){
        isHooked = hook();
        if (isHooked) {
            DailyRewardsPlugin.get().getPluginManager().registerEvents(new ItemsAdderLoadDataListener(), DailyRewardsPlugin.get());
        }
    }

    private boolean hook(){
        return VersionUtils.isLoaded("ItemsAdder");
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
