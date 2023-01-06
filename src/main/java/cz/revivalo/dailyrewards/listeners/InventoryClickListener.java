package cz.revivalo.dailyrewards.listeners;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.managers.MenuManager;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

	@Getter
	public static final InventoryClickListener instance = new InventoryClickListener();

	@EventHandler(ignoreCancelled = true)
	public void inventoryClick(final InventoryClickEvent event) {
		if (!(event.getInventory().getHolder() instanceof MenuManager.RewardsInventoryHolder)) return;
		if (event.getCurrentItem() == null) return;
		event.setCancelled(true);

		final RewardType rewardType = RewardType.findByCooldown(event.getSlot());
		if (rewardType == null) return;
		DailyRewards.getRewardManager().claim((Player) event.getWhoClicked(), rewardType, false, true);
	}
}
