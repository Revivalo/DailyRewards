package dev.revivalo.dailyrewards.listeners;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.MenuManager;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class InventoryClickListener implements Listener {

	public static final InventoryClickListener instance = new InventoryClickListener();

	@EventHandler (ignoreCancelled = true)
	public void onInventoryClick(final InventoryClickEvent event){
		if (!(event.getInventory().getHolder() instanceof MenuManager.RewardsInventoryHolder)) return;
		if (event.getCurrentItem() == null) return;
		event.setCancelled(true);
		final Player player = (Player) event.getWhoClicked();
		int slot = event.getSlot();
		if (slot == Config.DAILY_POSITION.asInt()){
			new ClaimAction(player).preCheck(player, RewardType.DAILY, false);
			//DailyRewardsPlugin.getRewardManager().claim(player, RewardType.DAILY, false, true);
		} else if (slot == Config.WEEKLY_POSITION.asInt()){
			new ClaimAction(player).preCheck(player, RewardType.WEEKLY, false);
		} else if (slot == Config.MONTHLY_POSITION.asInt()){
			new ClaimAction(player).preCheck(player, RewardType.MONTHLY, false);
		} else if (slot == Config.SETTINGS_POSITION.asInt()) {
			DailyRewardsPlugin.getMenuManager().openSettings(player);
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void inventoryClick(final InventoryClickEvent event) {
		if (!(event.getInventory().getHolder() instanceof MenuManager.RewardSettingsInventoryHolder))
			return;

		if (event.getCurrentItem() == null)
			return;

		event.setCancelled(true);

		final User user = UserHandler.getUser(event.getWhoClicked().getUniqueId());
		final Player player = user.getPlayer();

		int slot = event.getSlot();
		if (slot == Config.JOIN_NOTIFICATION_POSITION.asInt()) {
			if (!PermissionUtils.hasPermission(player, PermissionUtils.Permission.JOIN_NOTIFICATION_SETTING)) {
				player.sendMessage(Lang.INSUFFICIENT_PERMISSION_MESSAGE.asColoredString());
				return;
			}

			user.setEnabledJoinNotification(!user.hasEnabledJoinNotification());
			DataManager.updateValues(user.getPlayer().getUniqueId(), user, new HashMap<String, Object>(){{put("joinNotification", user.hasEnabledJoinNotification() ? 1L : 0);}});
			DailyRewardsPlugin.getMenuManager().openSettings(user.getPlayer());
		} else if (slot == Config.AUTO_CLAIM_REWARDS_POSITION.asInt()) {
			if (!PermissionUtils.hasPermission(player, PermissionUtils.Permission.AUTO_CLAIM_SETTING)) {
				player.sendMessage(Lang.INSUFFICIENT_PERMISSION_MESSAGE.asColoredString());
				return;
			}

			user.setEnabledAutoClaim(!user.hasEnabledAutoClaim());
			DataManager.updateValues(user.getPlayer().getUniqueId(), user, new HashMap<String, Object>(){{put("autoClaim", user.hasEnabledAutoClaim() ? 1L : 0);}});
			DailyRewardsPlugin.getMenuManager().openSettings(user.getPlayer());
		} else if (slot == Config.SETTINGS_POSITION.asInt()) {
			DailyRewardsPlugin.getMenuManager().openRewardsMenu(user.getPlayer());
		}
	}

	public static InventoryClickListener getInstance() {
		return instance;
	}
}
