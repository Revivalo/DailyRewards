package cz.revivalo.dailyrewards.guimanager;

import cz.revivalo.dailyrewards.guimanager.holders.Rewards;
import cz.revivalo.dailyrewards.files.Lang;
import cz.revivalo.dailyrewards.rewardmanager.RewardManager;
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
        if (event.getInventory().getHolder() instanceof Rewards){
            if (event.getCurrentItem() == null) return;
            event.setCancelled(true);
            final Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            if (slot == Lang.DAILY_POSITION.getInt()){
                rewardManager.claim(player, "daily", false);
            } else if (slot == Lang.WEEKLY_POSITION.getInt()){
                rewardManager.claim(player, "weekly", false);
            } else if (slot == Lang.MONTHLY_POSITION.getInt()){
                rewardManager.claim(player, "monthly", false);
            }
        }
    }
}
