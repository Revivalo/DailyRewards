package cz.revivalo.dailyrewards.guimanager;

import cz.revivalo.dailyrewards.guimanager.holders.Rewards;
import cz.revivalo.dailyrewards.lang.Lang;
import cz.revivalo.dailyrewards.rewardmanager.RewardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickEvent implements Listener {
    private final RewardManager rewardManager;
    public ClickEvent(RewardManager rewardManager) {
        this.rewardManager = rewardManager;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        if (e.getInventory().getHolder() instanceof Rewards){
            if (e.getCurrentItem() == null) return;
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            int slot = e.getSlot();
            if (slot == Integer.parseInt(Lang.DAILYPOSITION.content(null))){
                rewardManager.claim(p, "daily", false);
            } else if (slot == Integer.parseInt(Lang.WEEKLYPOSITION.content(null))){
                rewardManager.claim(p, "weekly", false);
            } else if (slot == Integer.parseInt(Lang.MONTHLYPOSITION.content(null))){
                rewardManager.claim(p, "monthly", false);
            }
        }
    }
}
