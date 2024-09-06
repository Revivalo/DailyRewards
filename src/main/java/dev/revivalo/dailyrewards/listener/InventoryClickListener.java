package dev.revivalo.dailyrewards.listener;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.file.Config;
import dev.revivalo.dailyrewards.configuration.file.Lang;
import dev.revivalo.dailyrewards.manager.MenuManager;
import dev.revivalo.dailyrewards.manager.Setting;
import dev.revivalo.dailyrewards.manager.reward.RewardType;
import dev.revivalo.dailyrewards.manager.reward.action.ClaimAction;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.util.PermissionUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

	public InventoryClickListener() {
		DailyRewardsPlugin.get().registerListeners(this);
	}

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
			if (!PermissionUtil.hasPermission(player, PermissionUtil.Permission.JOIN_NOTIFICATION_SETTING)) {
				player.sendMessage(Lang.PERMISSION_MSG.asColoredString(player));
				return;
			}

			user.toggleSetting(Setting.JOIN_NOTIFICATION, !user.hasSettingEnabled(Setting.JOIN_NOTIFICATION));

			DailyRewardsPlugin.getMenuManager().openSettings(user.getPlayer());
		} else if (slot == Config.AUTO_CLAIM_REWARDS_POSITION.asInt()) {
			if (!PermissionUtil.hasPermission(player, PermissionUtil.Permission.AUTO_CLAIM_SETTING)) {
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