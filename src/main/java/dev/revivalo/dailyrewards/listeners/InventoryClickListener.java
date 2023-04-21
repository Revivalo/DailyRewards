package dev.revivalo.dailyrewards.listeners;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.managers.MenuManager;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class InventoryClickListener implements Listener {

	@Getter public static final InventoryClickListener instance = new InventoryClickListener();

	@EventHandler (ignoreCancelled = true)
	public void onInventoryClick(final InventoryClickEvent event){
		if (!(event.getInventory().getHolder() instanceof MenuManager.RewardsInventoryHolder)) return;
		if (event.getCurrentItem() == null) return;
		event.setCancelled(true);
		final Player player = (Player) event.getWhoClicked();
		int slot = event.getSlot();
		if (slot == Config.DAILY_POSITION.asInt() && Config.DAILY_ENABLED.asBoolean()){
			DailyRewardsPlugin.getRewardManager().claim(player, RewardType.DAILY, false, true);
		} else if (slot == Config.WEEKLY_POSITION.asInt() && Config.WEEKLY_ENABLED.asBoolean()){
			DailyRewardsPlugin.getRewardManager().claim(player, RewardType.WEEKLY, false, true);
		} else if (slot == Config.MONTHLY_POSITION.asInt() && Config.MONTHLY_ENABLED.asBoolean()){
			DailyRewardsPlugin.getRewardManager().claim(player, RewardType.MONTHLY, false, true);
		} else if (slot == Config.SETTINGS_POSITION.asInt()) {
			DailyRewardsPlugin.getMenuManager().openSettings(player, true);
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void inventoryClick(final InventoryClickEvent event) {
		if (!(event.getInventory().getHolder() instanceof MenuManager.RewardSettingsInventoryHolder)) return;
		if (event.getCurrentItem() == null) return;
		event.setCancelled(true);
		final User user = UserHandler.getUser(event.getWhoClicked().getUniqueId());
		int slot = event.getSlot();
		if (slot == Config.JOIN_NOTIFICATION_POSITION.asInt()) {
			user.setEnabledJoinNotification(!user.isEnabledJoinNotification());
			DataManager.updateValues(user.getPlayer().getUniqueId(), user, new HashMap<String, Long>(){{put("join-notification", user.isEnabledJoinNotification() ? 1L : 0);}});
			DailyRewardsPlugin.getMenuManager().openSettings(user.getPlayer(), true);
		} else if (slot == Config.AUTO_CLAIM_REWARDS_POSITION.asInt()) {
			user.setEnabledAutoClaim(!user.isEnabledAutoClaim());
			DataManager.updateValues(user.getPlayer().getUniqueId(), user, new HashMap<String, Long>(){{put("auto-claim", user.isEnabledAutoClaim() ? 1L : 0);}});
			DailyRewardsPlugin.getMenuManager().openSettings(user.getPlayer(), true);
		} else if (slot == Config.SETTINGS_POSITION.asInt()) {
			DailyRewardsPlugin.getMenuManager().openRewardsMenu(user.getPlayer());
		}
	}
}
