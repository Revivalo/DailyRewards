package cz.revivalo.dailyrewards.guimanager;

import cz.revivalo.dailyrewards.guimanager.holders.Rewards;
import cz.revivalo.dailyrewards.lang.Lang;
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
    public void onMenuClick(final InventoryClickEvent e){
        if (e.getInventory().getHolder() instanceof Rewards){
            if (e.getCurrentItem() == null) return;
            e.setCancelled(true);
            final Player player = (Player) e.getWhoClicked();
            int slot = e.getSlot();
            if (slot == Integer.parseInt(Lang.DAILYPOSITION.content(null))){
                rewardManager.claim(player, "daily", false);
            } else if (slot == Integer.parseInt(Lang.WEEKLYPOSITION.content(null))){
                rewardManager.claim(player, "weekly", false);
            } else if (slot == Integer.parseInt(Lang.MONTHLYPOSITION.content(null))){
                rewardManager.claim(player, "monthly", false);
            }
        }
    }
}
