package dev.revivalo.dailyrewards.listeners;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.YamlFile;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class ItemsAdderLoadDataListener implements Listener {
    @EventHandler
    public void onItemsAdderLoad(final ItemsAdderLoadDataEvent event){
        Config.loadItems(Objects.requireNonNull(new YamlFile("config.yml",
                DailyRewardsPlugin.get().getDataFolder())
                .getConfiguration().getConfigurationSection("config")));
    }
}
