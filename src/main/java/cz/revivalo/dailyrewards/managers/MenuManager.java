package cz.revivalo.dailyrewards.managers;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.configuration.enums.Config;
import cz.revivalo.dailyrewards.configuration.enums.Lang;
import cz.revivalo.dailyrewards.managers.cooldown.Cooldown;
import cz.revivalo.dailyrewards.managers.cooldown.CooldownManager;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
import cz.revivalo.dailyrewards.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class MenuManager {
	private final ItemStack BACKGROUND_ITEM = new ItemBuilder(Config.BACKGROUND_ITEM.asAnItem()).setName(" ").build();
	public void openRewardsMenu(final Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(DailyRewards.getPlugin(), () -> {
			final Inventory inventory = Bukkit.createInventory(
					new RewardsInventoryHolder(),
					Config.MENU_SIZE.asInt(),
					Lang.MENU_TITLE.asColoredString());

			if (Config.FILL_BACKGROUND.asBoolean()) {
				for (int i = 0; i < Config.MENU_SIZE.asInt(); i++)
					inventory.setItem(i, BACKGROUND_ITEM);
			}


			if (Config.DAILY_ENABLED.asBoolean()) {
				final Cooldown dailyCooldown = CooldownManager.getCooldown(player, RewardType.DAILY);
				final AtomicLong dailyCooldownLong = new AtomicLong(dailyCooldown.getTimeLeft());
				final AtomicReference<BukkitTask> atomicTask = new AtomicReference<>();

				atomicTask.set(Bukkit.getScheduler().runTaskTimerAsynchronously(DailyRewards.getPlugin(), () -> {
					if (!player.getOpenInventory().getTitle().equalsIgnoreCase(Lang.MENU_TITLE.asColoredString())) {
						atomicTask.get().cancel();
						return;
					}

					dailyCooldown.setTimeLeft(dailyCooldownLong.addAndGet(-1000));

					boolean claimable = dailyCooldownLong.get() <= 0;
					inventory.setItem(Config.DAILY_POSITION.asInt(),
							new ItemBuilder(claimable ?
									Config.DAILY_AVAILABLE_ITEM.asAnItem() :
									Config.DAILY_UNAVAILABLE_ITEM.asAnItem())
									.setGlow(claimable)
									.setName(
											claimable ?
													Lang.DAILY_DISPLAY_NAME_AVAILABLE.asPlaceholderReplacedText(player) :
													Lang.DAILY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderReplacedText(player))
									.setLore(
											claimable ?
													Lang.valueOf(String.format("DAILY_AVAILABLE%s_LORE", DailyRewards.isPremium(player, RewardType.DAILY))).asColoredList(Collections.emptyMap()) :
													Lang.DAILY_UNAVAILABLE_LORE.asColoredList(new HashMap<String, String>() {{
														put("%cooldown%", dailyCooldown.getFormat(Config.DAILY_COOLDOWN_FORMAT.asString()));
													}})
									).build()
					);
				}, 0, 20));
			}

			if (Config.WEEKLY_ENABLED.asBoolean()) {
				final Cooldown weeklyCooldown = CooldownManager.getCooldown(player, RewardType.WEEKLY);
				inventory.setItem(Config.WEEKLY_POSITION.asInt(),
						new ItemBuilder(weeklyCooldown.isClaimable() ?
								Config.WEEKLY_AVAILABLE_ITEM.asAnItem() :
								Config.WEEKLY_UNAVAILABLE_ITEM.asAnItem())
								.setGlow(weeklyCooldown.isClaimable())
								.setName(
										weeklyCooldown.isClaimable() ?
												Lang.WEEKLY_DISPLAY_NAME_AVAILABLE.asPlaceholderReplacedText(player) :
												Lang.WEEKLY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderReplacedText(player))
								.setLore(
										weeklyCooldown.isClaimable() ?
												Lang.valueOf(String.format("WEEKLY_AVAILABLE%s_LORE", DailyRewards.isPremium(player, RewardType.WEEKLY))).asColoredList(Collections.emptyMap()) :
												Lang.WEEKLY_UNAVAILABLE_LORE.asColoredList(new HashMap<String, String>() {{
													put("%cooldown%", weeklyCooldown.getFormat(Config.WEEKLY_COOLDOWN_FORMAT.asString()));
												}})
								).build()
				);
			}

			if (Config.MONTHLY_ENABLED.asBoolean()) {
				final Cooldown monthlyCooldown = CooldownManager.getCooldown(player, RewardType.MONTHLY);
				inventory.setItem(Config.MONTHLY_POSITION.asInt(),
						new ItemBuilder(monthlyCooldown.isClaimable() ?
								Config.MONTHLY_AVAILABLE_ITEM.asAnItem() :
								Config.MONTHLY_UNAVAILABLE_ITEM.asAnItem())
								.setGlow(monthlyCooldown.isClaimable())
								.setName(
										monthlyCooldown.isClaimable() ?
												Lang.MONTHLY_DISPLAY_NAME_AVAILABLE.asPlaceholderReplacedText(player) :
												Lang.MONTHLY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderReplacedText(player))
								.setLore(
										monthlyCooldown.isClaimable() ?
												Lang.valueOf(String.format("MONTHLY_AVAILABLE%s_LORE", DailyRewards.isPremium(player, RewardType.MONTHLY))).asColoredList(Collections.emptyMap()) :
												Lang.MONTHLY_UNAVAILABLE_LORE.asColoredList(new HashMap<String, String>() {{
													put("%cooldown%", monthlyCooldown.getFormat(Config.MONTHLY_COOLDOWN_FORMAT.asString()));
												}})
								).build()
				);
			}

			player.openInventory(inventory);
		});
	}

	public static class RewardsInventoryHolder implements InventoryHolder {
		@Override
		public Inventory getInventory() {
			return null;
		}
	}
}
