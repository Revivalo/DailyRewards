package cz.revivalo.dailyrewards.managers;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.configuration.enums.Config;
import cz.revivalo.dailyrewards.configuration.enums.Lang;
import cz.revivalo.dailyrewards.managers.cooldown.Cooldown;
import cz.revivalo.dailyrewards.managers.cooldown.CooldownManager;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class MenuManager {

	public void openRewardsMenu(final Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(DailyRewards.getPlugin(), () -> {
			final Inventory inventory = Bukkit.createInventory(
					new RewardsInventoryHolder(),
					Config.MENU_SIZE.asInt(),
					Lang.MENU_TITLE.asColoredString());

			if (Config.FILL_BACKGROUND.asBoolean()) {
				for (int i = 0; i < Config.MENU_SIZE.asInt(); i++)
					inventory.setItem(i, this.createItemGui(Config.BACKGROUND_ITEM.asUppercase(), false, " ", null));
			}

			final Cooldown dailyCooldown = CooldownManager.getCooldown(player, RewardType.DAILY);
			inventory.setItem(Config.DAILY_POSITION.asInt(),
					this.createItemGui(
							dailyCooldown.isClaimable() ?
									Config.DAILY_AVAILABLE_ITEM.asUppercase() :
									Config.DAILY_UNAVAILABLE_ITEM.asUppercase(),
							dailyCooldown.isClaimable(), dailyCooldown.isClaimable() ?
									Lang.DAILY_DISPLAY_NAME_AVAILABLE.asPlaceholderReplacedText(player) :
									Lang.DAILY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderReplacedText(player),
							dailyCooldown.isClaimable() ?
									Lang.valueOf(String.format("DAILY_AVAILABLE%s_LORE", DailyRewards.isPremium(player, RewardType.DAILY))).asColoredList() :
									Lang.DAILY_UNAVAILABLE_LORE.asColoredList("%cooldown%", dailyCooldown.getFormat())
					)
			);

			final AtomicLong dailyCooldownLong = new AtomicLong(dailyCooldown.getTimeLeft());
			final AtomicReference<BukkitTask> atomicTask = new AtomicReference<>();

			atomicTask.set(Bukkit.getScheduler().runTaskTimerAsynchronously(DailyRewards.getPlugin(), () -> {
				dailyCooldownLong.addAndGet(-1000);

				if (!player.getOpenInventory().getTitle().equalsIgnoreCase(Lang.MENU_TITLE.asColoredString())) {
					atomicTask.get().cancel();
					return;
				}

				boolean claimable = dailyCooldownLong.get() <= 0;
				inventory.setItem(Config.DAILY_POSITION.asInt(),
						createItemGui(
								claimable ?
										Config.DAILY_AVAILABLE_ITEM.asUppercase() :
										Config.DAILY_UNAVAILABLE_ITEM.asUppercase(), claimable,
								claimable ?
										Lang.DAILY_DISPLAY_NAME_AVAILABLE.asPlaceholderReplacedText(player) :
										Lang.DAILY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderReplacedText(player),
								claimable ?
										Lang.valueOf(String.format("DAILY_AVAILABLE%s_LORE", DailyRewards.isPremium(player, RewardType.DAILY))).asColoredList() :
										Lang.DAILY_UNAVAILABLE_LORE.asColoredList("%cooldown%", Config.format(dailyCooldownLong.get()))
						)
				);
			}, 0, 20));

			final Cooldown weeklyCooldown = CooldownManager.getCooldown(player, RewardType.WEEKLY);
			inventory.setItem(Config.WEEKLY_POSITION.asInt(),
					this.createItemGui(
							weeklyCooldown.isClaimable() ?
									Config.WEEKLY_AVAILABLE_ITEM.asUppercase() :
									Config.WEEKLY_UNAVAILABLE_ITEM.asUppercase(),
							weeklyCooldown.isClaimable(), weeklyCooldown.isClaimable() ?
									Lang.WEEKLY_DISPLAY_NAME_AVAILABLE.asPlaceholderReplacedText(player) :
									Lang.WEEKLY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderReplacedText(player),
							weeklyCooldown.isClaimable() ?
									Lang.valueOf(String.format("WEEKLY_AVAILABLE%s_LORE", DailyRewards.isPremium(player, RewardType.WEEKLY))).asColoredList() :
									Lang.WEEKLY_UNAVAILABLE_LORE.asColoredList("%cooldown%", weeklyCooldown.getFormat())
					)
			);

			final Cooldown monthlyCooldown = CooldownManager.getCooldown(player, RewardType.MONTHLY);
			inventory.setItem(Config.MONTHLY_POSITION.asInt(),
					this.createItemGui(
							monthlyCooldown.isClaimable() ?
									Config.MONTHLY_AVAILABLE_ITEM.asUppercase() :
									Config.MONTHLY_UNAVAILABLE_ITEM.asUppercase(),
							monthlyCooldown.isClaimable(), monthlyCooldown.isClaimable() ?
									Lang.MONTHLY_DISPLAYNAME_AVAILABLE.asPlaceholderReplacedText(player) :
									Lang.MONTHLY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderReplacedText(player),
							monthlyCooldown.isClaimable() ?
									Lang.valueOf(String.format("MONTHLY_AVAILABLE%s_LORE", DailyRewards.isPremium(player, RewardType.WEEKLY))).asColoredList() :
									Lang.MONTHLY_UNAVAILABLE_LORE.asColoredList("%cooldown%", monthlyCooldown.getFormat())));

			player.openInventory(inventory);
		});
	}

	private ItemStack createItemGui(String material, boolean glow, String name, List<String> lore) {
		final ItemStack item = new ItemStack(Objects.requireNonNull(Material.getMaterial(material)));
		final ItemMeta itemMeta = item.getItemMeta();
		if (itemMeta == null) return item;

		itemMeta.setDisplayName(name);
		itemMeta.setLore(lore);

		if (material.equalsIgnoreCase("PLAYER_HEAD")) {
			((SkullMeta) itemMeta).setOwningPlayer(null);
		} else if (glow) {
			itemMeta.addEnchant(Enchantment.LURE, 1, false);
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		item.setItemMeta(itemMeta);
		return item;
	}

	public static class RewardsInventoryHolder implements InventoryHolder {

		@SuppressWarnings("ConstantConditions")
		@Override
		public Inventory getInventory() {
			return null;
		}
	}
}
