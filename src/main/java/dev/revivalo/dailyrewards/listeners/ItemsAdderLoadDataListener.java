package dev.revivalo.dailyrewards.listeners;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderLoadDataListener implements Listener {
    @EventHandler
    public void onItemsAdderLoad(final ItemsAdderLoadDataEvent event){
        DailyRewardsPlugin.get().reloadPlugin();
    }
}
