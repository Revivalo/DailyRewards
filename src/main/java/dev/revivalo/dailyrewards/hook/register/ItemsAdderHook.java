package dev.revivalo.dailyrewards.hook.register;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.hook.Hook;
import dev.revivalo.dailyrewards.util.VersionUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook implements Hook<Void>, Listener {
    private boolean isHooked;

    private boolean hook(){
        return VersionUtil.isLoaded("ItemsAdder");
    }

    @Override
    public void register() {
        isHooked = hook();
        if (isHooked) {
            DailyRewardsPlugin.get().registerListeners(this);
        }
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Override
    public @Nullable Void getApi() {
        return null;
    }

    @EventHandler
    public void onItemsAdderLoad(final ItemsAdderLoadDataEvent event){
        DailyRewardsPlugin.get().reloadPlugin();
    }
}
