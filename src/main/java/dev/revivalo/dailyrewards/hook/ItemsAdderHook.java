package dev.revivalo.dailyrewards.hook;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.listener.ItemsAdderLoadDataListener;
import dev.revivalo.dailyrewards.util.VersionUtil;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook implements IHook<Void> {
    private final boolean isHooked;
    ItemsAdderHook(){
        isHooked = hook();
        if (isHooked) {
            DailyRewardsPlugin.get().getPluginManager().registerEvents(new ItemsAdderLoadDataListener(), DailyRewardsPlugin.get());
        }
    }

    private boolean hook(){
        return VersionUtil.isLoaded("ItemsAdder");
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
