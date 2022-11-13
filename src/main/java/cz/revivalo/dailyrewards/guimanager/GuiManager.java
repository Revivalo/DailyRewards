package cz.revivalo.dailyrewards.guimanager;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.guimanager.holders.Rewards;
import cz.revivalo.dailyrewards.files.Lang;
import cz.revivalo.dailyrewards.rewardmanager.Cooldown;
import cz.revivalo.dailyrewards.rewardmanager.Cooldowns;

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
    private final Cooldowns cooldowns;
    public GuiManager(final DailyRewards plugin){
        this.plugin = plugin;
        cooldowns = plugin.getCooldowns();
    }

    public void openRewardsMenu(final Player player){
        Bukkit.getScheduler().scheduleSyncDelayedTask(DailyRewards.getPlugin(DailyRewards.class), () -> {
            final Inventory inv = Bukkit.createInventory(new Rewards(), Integer.parseInt(Lang.MENU_SIZE.content(player)), Lang.MENU_TITLE.content(player));
            if (Lang.FILL_BACKGROUND.getBoolean()) {
                for (int i = 0; i < Lang.MENU_SIZE.getInt(); i++) {
                    inv.setItem(i, createGuiItem(Lang.BACKGROUND_ITEM.content(player).toUpperCase(), false, " ", null));
                }
            }

            final Cooldown dailyCooldown = cooldowns.getCooldown(player, "daily");
            final Cooldown weeklyCooldown = cooldowns.getCooldown(player, "weekly");
            final Cooldown monthlyCooldown = cooldowns.getCooldown(player, "monthly");
            inv.setItem(Integer.parseInt(Lang.DAILY_POSITION.content(null)), createGuiItem(dailyCooldown.isClaimable() ? Lang.DAILY_AVAILABLE_ITEM.getTextInUppercase() : Lang.DAILY_UNAVAILABLE_ITEM.getTextInUppercase(), dailyCooldown.isClaimable(),dailyCooldown.isClaimable() ? Lang.DAILY_DISPLAY_NAME_AVAILABLE.content(player) : Lang.DAILY_DISPLAY_NAME_UNAVAILABLE.content(player), dailyCooldown.isClaimable() ? Lang.valueOf("DAILY_AVAILABLE" + plugin.getPremium(player, "daily") + "_LORE").getColoredList(player) : Lang.DAILY_UNAVAILABLE_LORE.getColoredList(player, "%cooldown%", dailyCooldown.getFormat())));
            new BukkitRunnable() {

                @Override
                public void run() {
                    if (player.getOpenInventory().getTitle().equalsIgnoreCase(Lang.MENU_TITLE.getColoredText())) {
                        inv.setItem(Lang.DAILY_POSITION.getInt(), createGuiItem(cooldowns.getCooldown(player, "daily").isClaimable() ? Lang.DAILY_AVAILABLE_ITEM.getTextInUppercase() : Lang.DAILY_UNAVAILABLE_ITEM.getTextInUppercase(), cooldowns.getCooldown(player, "daily").isClaimable(), cooldowns.getCooldown(player, "daily").isClaimable() ? Lang.DAILY_DISPLAY_NAME_AVAILABLE.content(player) : Lang.DAILY_DISPLAY_NAME_UNAVAILABLE.content(player), cooldowns.getCooldown(player, "daily").isClaimable() ? Lang.valueOf("DAILY_AVAILABLE" + plugin.getPremium(player, "daily") + "_LORE").getColoredList(player) : Lang.DAILY_UNAVAILABLE_LORE.getColoredList(player, "%cooldown%", cooldowns.getCooldown(player, "daily").getFormat())));
                    } else {
                        cancel();
                    }

                }
            }.runTaskTimerAsynchronously(plugin, 0, 20);
            inv.setItem(Lang.WEEKLY_POSITION.getInt(), createGuiItem(weeklyCooldown.isClaimable() ? Lang.WEEKLY_AVAILABLE_ITEM.getTextInUppercase() : Lang.WEEKLY_UNAVAILABLE_ITEM.getTextInUppercase(), weeklyCooldown.isClaimable(), weeklyCooldown.isClaimable() ? Lang.WEEKLY_DISPLAY_NAME_AVAILABLE.content(player) : Lang.WEEKLY_DISPLAY_NAME_UNAVAILABLE.content(player), weeklyCooldown.isClaimable() ? Lang.valueOf("WEEKLY_AVAILABLE" + plugin.getPremium(player, "weekly") + "_LORE").getColoredList(player) : Lang.WEEKLY_UNAVAILABLE_LORE.getColoredList(player, "%cooldown%", weeklyCooldown.getFormat())));
            inv.setItem(Lang.MONTHLY_POSITION.getInt(), createGuiItem(monthlyCooldown.isClaimable() ? Lang.MONTHLY_AVAILABLE_ITEM.getTextInUppercase() : Lang.MONTHLY_UNAVAILABLE_ITEM.getTextInUppercase(), monthlyCooldown.isClaimable(), monthlyCooldown.isClaimable() ? Lang.MONTHLY_DISPLAYNAME_AVAILABLE.content(player) : Lang.MONTHLY_DISPLAY_NAME_UNAVAILABLE.content(player), monthlyCooldown.isClaimable() ? Lang.valueOf("MONTHLY_AVAILABLE" + plugin.getPremium(player, "monthly") + "_LORE").getColoredList(player) : Lang.MONTHLY_UNAVAILABLE_LORE.getColoredList(player, "%cooldown%", monthlyCooldown.getFormat())));
            player.openInventory(inv);
        });
    }

    private ItemStack createGuiItem(String id, boolean glow, String name, List<String> lore) {
        ItemStack item = new ItemStack(Objects.requireNonNull(Material.getMaterial(id)));
        ItemMeta meta = item.getItemMeta();
        if (id.equalsIgnoreCase("PLAYER_HEAD")) {
            meta = item.getItemMeta();
            if (meta != null) {
                ((SkullMeta) meta).setOwningPlayer(null);
            }
        }
        if (glow) {
            if (!id.equalsIgnoreCase("PLAYER_HEAD")){
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
