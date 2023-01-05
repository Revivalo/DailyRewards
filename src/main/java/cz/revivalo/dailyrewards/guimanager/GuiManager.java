package cz.revivalo.dailyrewards.guimanager;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.RewardType;
import cz.revivalo.dailyrewards.files.Config;
import cz.revivalo.dailyrewards.guimanager.holders.RewardsInventoryHolder;
import cz.revivalo.dailyrewards.files.Lang;
import cz.revivalo.dailyrewards.managers.Cooldown;
import cz.revivalo.dailyrewards.managers.Cooldowns;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public class GuiManager {
    private final DailyRewards plugin;
    public GuiManager(final DailyRewards plugin){
        this.plugin = plugin;
    }

    public void openRewardsMenu(final Player player){
        Bukkit.getScheduler().scheduleSyncDelayedTask(DailyRewards.getPlugin(DailyRewards.class), () -> {
            final Inventory inv = Bukkit.createInventory(new RewardsInventoryHolder(), Config.MENU_SIZE.asInt(), Lang.MENU_TITLE.asColoredString());
            if (Config.FILL_BACKGROUND.asBoolean()) {
                for (int i = 0; i < Config.MENU_SIZE.asInt(); i++) {
                    inv.setItem(i, createGuiItem(Config.BACKGROUND_ITEM.asStringInUppercase(), false, " ", null));
                }
            }

            final Cooldown dailyCooldown = Cooldowns.getCooldown(player, RewardType.DAILY);
            final Cooldown weeklyCooldown = Cooldowns.getCooldown(player, RewardType.WEEKLY);
            final Cooldown monthlyCooldown = Cooldowns.getCooldown(player, RewardType.MONTHLY);

            //final long dailyCooldownLong = dailyCooldown.getTimeLeft();

            inv.setItem(Config.DAILY_POSITION.asInt(),
                    createGuiItem(
                            dailyCooldown.isClaimable()
                                    ? Config.DAILY_AVAILABLE_ITEM.asStringInUppercase()
                                    : Config.DAILY_UNAVAILABLE_ITEM.asStringInUppercase(),
                            dailyCooldown.isClaimable(),dailyCooldown.isClaimable() ? Lang.DAILY_DISPLAY_NAME_AVAILABLE.asPlaceholderApiReplacedString(player) : Lang.DAILY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderApiReplacedString(player),
                            dailyCooldown.isClaimable()
                                    ? Lang.valueOf("DAILY_AVAILABLE" + plugin.getPremium(player, RewardType.DAILY) + "_LORE").asColoredList()
                                    : Lang.DAILY_UNAVAILABLE_LORE.asColoredList("%cooldown%", dailyCooldown.getFormat())
                    )
            );
            new BukkitRunnable() {
                long dailyCooldownLong = dailyCooldown.getTimeLeft();
                @Override
                public void run() {
                    dailyCooldownLong -= 1000;
                    boolean claimable = dailyCooldownLong <= 0;
                    if (player.getOpenInventory().getTitle().equalsIgnoreCase(Lang.MENU_TITLE.asColoredString())) {
                        inv.setItem(Config.DAILY_POSITION.asInt(),
                                createGuiItem(claimable
                                        ? Config.DAILY_AVAILABLE_ITEM.asStringInUppercase()
                                        : Config.DAILY_UNAVAILABLE_ITEM.asStringInUppercase(),
                                        claimable,
                                        claimable
                                                ? Lang.DAILY_DISPLAY_NAME_AVAILABLE.asPlaceholderApiReplacedString(player)
                                                : Lang.DAILY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderApiReplacedString(player),
                                        claimable
                                                ? Lang.valueOf("DAILY_AVAILABLE" + plugin.getPremium(player, RewardType.DAILY) + "_LORE").asColoredList()
                                                : Lang.DAILY_UNAVAILABLE_LORE.asColoredList("%cooldown%", Config.format(dailyCooldownLong))
                                )
                        );
                    } else {
                        cancel();
                    }

                }
            }.runTaskTimerAsynchronously(plugin, 0, 20);
            inv.setItem(Config.WEEKLY_POSITION.asInt(),
                    createGuiItem(
                            weeklyCooldown.isClaimable()
                                    ? Config.WEEKLY_AVAILABLE_ITEM.asStringInUppercase()
                                    : Config.WEEKLY_UNAVAILABLE_ITEM.asStringInUppercase(),
                            weeklyCooldown.isClaimable(), weeklyCooldown.isClaimable()
                                    ? Lang.WEEKLY_DISPLAY_NAME_AVAILABLE.asPlaceholderApiReplacedString(player)
                                    : Lang.WEEKLY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderApiReplacedString(player),
                            weeklyCooldown.isClaimable()
                                    ? Lang.valueOf("WEEKLY_AVAILABLE" + plugin.getPremium(player, RewardType.WEEKLY) + "_LORE").asColoredList()
                                    : Lang.WEEKLY_UNAVAILABLE_LORE.asColoredList("%cooldown%", weeklyCooldown.getFormat())
                    )
            );
            inv.setItem(Config.MONTHLY_POSITION.asInt(), createGuiItem(monthlyCooldown.isClaimable() ? Config.MONTHLY_AVAILABLE_ITEM.asStringInUppercase() : Config.MONTHLY_UNAVAILABLE_ITEM.asStringInUppercase(), monthlyCooldown.isClaimable(), monthlyCooldown.isClaimable() ? Lang.MONTHLY_DISPLAYNAME_AVAILABLE.asPlaceholderApiReplacedString(player) : Lang.MONTHLY_DISPLAY_NAME_UNAVAILABLE.asPlaceholderApiReplacedString(player), monthlyCooldown.isClaimable() ? Lang.valueOf("MONTHLY_AVAILABLE" + plugin.getPremium(player, RewardType.WEEKLY) + "_LORE").asColoredList() : Lang.MONTHLY_UNAVAILABLE_LORE.asColoredList("%cooldown%", monthlyCooldown.getFormat())));
            player.openInventory(inv);
        });
    }

    private ItemStack createGuiItem(String material, boolean glow, String name, List<String> lore) {
        ItemStack item = new ItemStack(Objects.requireNonNull(Material.getMaterial(material)));
        ItemMeta meta = item.getItemMeta();
        if (material.equalsIgnoreCase("PLAYER_HEAD")) {
            meta = item.getItemMeta();
            if (meta != null) {
                ((SkullMeta) meta).setOwner(null);
            }
        }
        if (glow) {
            if (!material.equalsIgnoreCase("PLAYER_HEAD")){
                if (meta != null) {
                    meta.addEnchant(Enchantment.LURE, 1, false);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
            }
        }
        if (meta != null) {
            meta.setDisplayName(name);
            Objects.requireNonNull(meta).setLore(lore);
        }
        item.setItemMeta(meta);

        return item;
    }
}
