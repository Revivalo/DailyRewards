package dev.revivalo.dailyrewards.listeners;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.MenuManager;
import dev.revivalo.dailyrewards.managers.Setting;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.managers.reward.actions.ClaimAction;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

	public static final InventoryClickListener instance = new InventoryClickListener();

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event){
		if (!(event.getInventory().getHolder() instanceof MenuManager.RewardsInventoryHolder)) return;
		if (event.getCurrentItem() == null) return;
		event.setCancelled(true);
		final Player player = (Player) event.getWhoClicked();
		int slot = event.getSlot();
		if (Config.DAILY_POSITIONS.asIntegerList().contains(slot)){
			new ClaimAction(player).preCheck(player, RewardType.DAILY);
		} else if (Config.WEEKLY_POSITIONS.asIntegerList().contains(slot)) {
			new ClaimAction(player).preCheck(player, RewardType.WEEKLY);
		} else if (Config.MONTHLY_POSITIONS.asIntegerList().contains(slot)) {
			new ClaimAction(player).preCheck(player, RewardType.MONTHLY);
		} else if (slot == Config.SETTINGS_POSITION.asInt()) {
			DailyRewardsPlugin.getMenuManager().openSettings(player);
		}
	}

	@EventHandler
	public void inventoryClick(final InventoryClickEvent event) {
		if (!(event.getInventory().getHolder() instanceof MenuManager.RewardSettingsInventoryHolder)) {
			return;
		}

		if (event.getCurrentItem() == null) {
			return;
		}

		event.setCancelled(true);

		final User user = UserHandler.getUser(event.getWhoClicked().getUniqueId());
		final Player player = user.getPlayer();

		int slot = event.getSlot();
		if (slot == Config.JOIN_NOTIFICATION_POSITION.asInt()) {
			if (!PermissionUtils.hasPermission(player, PermissionUtils.Permission.JOIN_NOTIFICATION_SETTING)) {
				player.sendMessage(Lang.PERMISSION_MSG.asColoredString(player));
				return;
			}

			user.toggleSetting(Setting.JOIN_NOTIFICATION, !user.hasSettingEnabled(Setting.JOIN_NOTIFICATION));

			DailyRewardsPlugin.getMenuManager().openSettings(user.getPlayer());
		} else if (slot == Config.AUTO_CLAIM_REWARDS_POSITION.asInt()) {
			if (!PermissionUtils.hasPermission(player, PermissionUtils.Permission.AUTO_CLAIM_SETTING)) {
				player.sendMessage(Lang.PERMISSION_MSG.asColoredString(player));
				return;
			}

			user.toggleSetting(Setting.AUTO_CLAIM, !user.hasSettingEnabled(Setting.AUTO_CLAIM));

			DailyRewardsPlugin.getMenuManager().openSettings(user.getPlayer());
		} else if (slot == Config.SETTINGS_POSITION.asInt()) {
			DailyRewardsPlugin.getMenuManager().openRewardsMenu(user.getPlayer());
		}
	}

	public static InventoryClickListener getInstance() {
		return instance;
	}
}