package dev.revivalo.dailyrewards.managers;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.cooldown.Cooldown;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class MenuManager {
	private ItemStack backgroundItem;
	public MenuManager() {
		loadBackgroundFiller();
	}

	public void openRewardsMenu(final Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(DailyRewardsPlugin.get(), () -> {
			final int timer = 20;
			final Inventory inventory = Bukkit.createInventory(
					new RewardsInventoryHolder(),
					Config.MENU_SIZE.asInt(),
					Lang.MENU_TITLE.asPlaceholderReplacedText(player));

			if (Config.FILL_BACKGROUND.asBoolean()) {
				for (int i = 0; i < Config.MENU_SIZE.asInt(); i++)
					inventory.setItem(i, backgroundItem);
			}

			final User user = UserHandler.getUser(player.getUniqueId());

			if (Config.SETTINGS_ENABLED_IN_MENU.asBoolean() && Config.SETTINGS_POSITION.asInt() < Config.MENU_SIZE.asInt())
					inventory.setItem(Config.SETTINGS_POSITION.asInt(), ItemBuilder.from(Config.SETTINGS_ITEM.asAnItem()).setName(Lang.SETTINGS_DISPLAY_NAME.asColoredString()).build());

			if (Config.DAILY_ENABLED.asBoolean()) {
				final Cooldown dailyCooldown = user.getCooldownOfReward(RewardType.DAILY); //CooldownManager.getCooldown(player, RewardType.DAILY);
				final AtomicReference<BukkitTask> atomicTask = new AtomicReference<>();

				atomicTask.set(Bukkit.getScheduler().runTaskTimer(DailyRewardsPlugin.get(), () -> {
					if (!player.getOpenInventory().getTitle().equalsIgnoreCase(Lang.MENU_TITLE.asColoredString())) {
						atomicTask.get().cancel();
						return;
					}
					//dailyCooldown.reduce(timer*50);

					boolean claimable = dailyCooldown.getTimeLeftInMillis() <= 0;
					inventory.setItem(Config.DAILY_POSITION.asInt(),
							ItemBuilder.from(
											claimable
													? Config.DAILY_AVAILABLE_ITEM.asAnItem()
													: Config.DAILY_UNAVAILABLE_ITEM.asAnItem()
									)
									.setGlow(claimable)
									.setName(
											claimable
													? Lang.DAILY_DISPLAY_NAME_AVAILABLE.asPlaceholderReplacedText(player)
													: Lang.DAILY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderReplacedText(player)
									).setLore(
											claimable
													? Lang.valueOf(String.format("DAILY_AVAILABLE%s_LORE", DailyRewardsPlugin.isPremium(player, RewardType.DAILY))).asReplacedList(Collections.emptyMap())
													: Lang.DAILY_UNAVAILABLE_LORE.asReplacedList(new HashMap<String, String>() {{
												put("%cooldown%", dailyCooldown.getFormat(Config.DAILY_COOLDOWN_FORMAT.asString()));
											}})
									)
									.build()
					);
				}, 0, timer));
			}

			if (Config.WEEKLY_ENABLED.asBoolean()) {
				final Cooldown weeklyCooldown = user.getCooldownOfReward(RewardType.WEEKLY);
				//final Cooldown weeklyCooldown = CooldownManager.getCooldown(player, RewardType.WEEKLY);
				inventory.setItem(Config.WEEKLY_POSITION.asInt(),
						ItemBuilder.from(
										weeklyCooldown.isClaimable()
												? Config.WEEKLY_AVAILABLE_ITEM.asAnItem()
												: Config.WEEKLY_UNAVAILABLE_ITEM.asAnItem())
								.setGlow(weeklyCooldown.isClaimable())
								.setName(
										weeklyCooldown.isClaimable()
												? Lang.WEEKLY_DISPLAY_NAME_AVAILABLE.asPlaceholderReplacedText(player)
												: Lang.WEEKLY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderReplacedText(player)
								).setLore(
										weeklyCooldown.isClaimable()
												? Lang.valueOf(String.format("WEEKLY_AVAILABLE%s_LORE", DailyRewardsPlugin.isPremium(player, RewardType.WEEKLY))).asReplacedList(Collections.emptyMap())
												: Lang.WEEKLY_UNAVAILABLE_LORE.asReplacedList(new HashMap<String, String>() {{
											put("%cooldown%", weeklyCooldown.getFormat(Config.WEEKLY_COOLDOWN_FORMAT.asString()));
										}})
								)
								.build()
				);
			}


			if (Config.MONTHLY_ENABLED.asBoolean()) {
				final Cooldown monthlyCooldown = user.getCooldownOfReward(RewardType.MONTHLY);
				inventory.setItem(Config.MONTHLY_POSITION.asInt(),
						ItemBuilder.from(
										monthlyCooldown.isClaimable()
												? Config.MONTHLY_AVAILABLE_ITEM.asAnItem()
												: Config.MONTHLY_UNAVAILABLE_ITEM.asAnItem()
								)
								.setGlow(monthlyCooldown.isClaimable())
								.setName(
										monthlyCooldown.isClaimable()
												? Lang.MONTHLY_DISPLAY_NAME_AVAILABLE.asPlaceholderReplacedText(player)
												: Lang.MONTHLY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderReplacedText(player)
								).setLore(
										monthlyCooldown.isClaimable()
												? Lang.valueOf(String.format("MONTHLY_AVAILABLE%s_LORE", DailyRewardsPlugin.isPremium(player, RewardType.MONTHLY))).asReplacedList(Collections.emptyMap())
												: Lang.MONTHLY_UNAVAILABLE_LORE.asReplacedList(new HashMap<String, String>() {{
											put("%cooldown%", monthlyCooldown.getFormat(Config.MONTHLY_COOLDOWN_FORMAT.asString()));
										}})
								)
								.build()
				);
			}

			player.openInventory(inventory);
		});
	}

	public void openSettings(Player player) {
		if (!player.hasPermission("dailyreward.settings")) {
			player.sendMessage(Lang.PERMISSION_MESSAGE.asColoredString());
			return;
		}

		final Inventory settings = Bukkit.createInventory(
				new RewardSettingsInventoryHolder(),
				Config.MENU_SIZE.asInt(),
				Lang.SETTINGS_TITLE.asColoredString());

		if (Config.FILL_BACKGROUND.asBoolean()) {
			for (int i = 0; i < 44; i++)
				settings.setItem(i, backgroundItem);
		}

		final User user = UserHandler.getUser(player.getUniqueId());

		settings.setItem(Config.SETTINGS_BACK_POSITION.asInt(), ItemBuilder.from(Config.SETTINGS_BACK_ITEM.asAnItem()).setName(Lang.BACK.asColoredString()).build());

		settings.setItem(Config.JOIN_NOTIFICATION_POSITION.asInt(), ItemBuilder.from(Config.SETTINGS_JOIN_NOTIFICATION_ITEM.asAnItem())
				.setName(Lang.JOIN_NOTIFICATION_DISPLAY_NAME.asColoredString())
				.setGlow(user.hasEnabledJoinNotification())
				.setLore(
						user.hasEnabledJoinNotification()
								? Lang.JOIN_NOTIFICATION_ENABLED_LORE.asReplacedList(Collections.emptyMap())
								: Lang.JOIN_NOTIFICATION_DISABLED_LORE.asReplacedList(Collections.emptyMap())
				).build()
		);

		settings.setItem(Config.AUTO_CLAIM_REWARDS_POSITION.asInt(), ItemBuilder.from(new ItemStack(Config.SETTINGS_AUTO_CLAIM_ITEM.asAnItem()))
				.setName(Lang.AUTO_CLAIM_DISPLAY_NAME.asColoredString())
				.setGlow(user.hasEnabledAutoClaim())
				.setLore(
						user.hasEnabledAutoClaim()
								? Lang.AUTO_CLAIM_ENABLED_LORE.asReplacedList(Collections.emptyMap())
								: Lang.AUTO_CLAIM_DISABLED_LORE.asReplacedList(Collections.emptyMap())
				).build()
		);

		player.openInventory(settings);
	}

	public void loadBackgroundFiller() {
		ItemBuilder.ItemBuilderBuilder backgroundItemBuilder = ItemBuilder.from(Config.BACKGROUND_ITEM.asAnItem());

		if (backgroundItemBuilder.getType() != Material.AIR) {
			backgroundItemBuilder.setName(" ");
		}
		backgroundItem = backgroundItemBuilder.build();
	}

	public static class RewardSettingsInventoryHolder implements InventoryHolder {

		@Override
		public Inventory getInventory() {
			return null;
		}
	}

	public static class RewardsInventoryHolder implements InventoryHolder {
		@Override
		public Inventory getInventory() {
			return null;
		}
	}
}
