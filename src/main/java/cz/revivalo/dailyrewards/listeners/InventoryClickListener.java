package cz.revivalo.dailyrewards.listeners;

import cz.revivalo.dailyrewards.RewardType;
import cz.revivalo.dailyrewards.files.Config;
import cz.revivalo.dailyrewards.guimanager.holders.RewardsInventoryHolder;
import cz.revivalo.dailyrewards.managers.RewardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {
    private final RewardManager rewardManager;
    public InventoryClickListener(RewardManager rewardManager) {
        this.rewardManager = rewardManager;
    }

    @EventHandler (ignoreCancelled = true)
    public void onMenuClick(final InventoryClickEvent event){
        if (event.getInventory().getHolder() instanceof RewardsInventoryHolder){
            if (event.getCurrentItem() == null) return;
            event.setCancelled(true);
            final Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            if (slot == Config.DAILY_POSITION.asInt()){
                rewardManager.claim(player, RewardType.DAILY, false, true);
            } else if (slot == Config.WEEKLY_POSITION.asInt()){
                rewardManager.claim(player, RewardType.WEEKLY, false, true);
            } else if (slot == Config.MONTHLY_POSITION.asInt()){
                rewardManager.claim(player, RewardType.MONTHLY, false, true);
            }
        }
    }
}
