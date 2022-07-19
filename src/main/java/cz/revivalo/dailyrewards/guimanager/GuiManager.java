package cz.revivalo.dailyrewards.guimanager;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.guimanager.holders.Rewards;
import cz.revivalo.dailyrewards.lang.Lang;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GuiManager {
    private final DailyRewards plugin;
    private final Cooldowns cooldowns;
    public GuiManager(DailyRewards plugin){
        this.plugin = plugin;
        cooldowns = plugin.getCooldowns();
    }

    public Inventory openRewardsMenu(final Player player){
        Inventory inv = Bukkit.createInventory(new Rewards(), Integer.parseInt(Lang.MENUSIZE.content(player)), Lang.MENUTITLE.content(player));
        if (Lang.FILLBACKGROUND.getBoolean()){
            for (int i = 0; i < Lang.MENUSIZE.getInt(); i++){
                inv.setItem(i, createGuiItem(Lang.BACKGROUNDITEM.content(player).toUpperCase(), false, " ", null));
            }
        }
        inv.setItem(Integer.parseInt(Lang.DAILYPOSITION.content(null)), createGuiItem(Long.parseLong(cooldowns.getCooldown(player, "daily", false)) < 0 ? Lang.DAILYAVAILABLEITEM.content(null) : Lang.DAILYUNAVAILABLEITEM.content(null).toUpperCase(), Long.parseLong(cooldowns.getCooldown(player, "daily", false)) < 0, Long.parseLong(cooldowns.getCooldown(player, "daily", false)) < 0 ? Lang.DAILYDISPLAYNAMEAVAILABLE.content(player) : Lang.DAILYDISPLAYNAMEUNAVAILABLE.content(player), Long.parseLong(cooldowns.getCooldown(player, "daily", false)) < 0 ? replace(player, Lang.valueOf("DAILYAVAILABLE" + plugin.getPremium(player, "daily") + "LORE").contentLore(player), "daily") : replace(player, Lang.DAILYUNAVAILABLELORE.contentLore(player), "daily")));
        new BukkitRunnable() {

            @Override
            public void run() {
                if (player.getOpenInventory().getTitle().equalsIgnoreCase(Lang.MENUTITLE.content(player))) {
                    inv.setItem(Lang.DAILYPOSITION.getInt(), createGuiItem(Long.parseLong(cooldowns.getCooldown(player, "daily", false)) < 0 ? Lang.DAILYAVAILABLEITEM.content(null) : Lang.DAILYUNAVAILABLEITEM.content(null).toUpperCase(), Long.parseLong(cooldowns.getCooldown(player, "daily", false)) < 0, Long.parseLong(cooldowns.getCooldown(player, "daily", false)) < 0 ? Lang.DAILYDISPLAYNAMEAVAILABLE.content(player) : Lang.DAILYDISPLAYNAMEUNAVAILABLE.content(player), Long.parseLong(cooldowns.getCooldown(player, "daily", false)) < 0 ? replace(player, Lang.valueOf("DAILYAVAILABLE" + plugin.getPremium(player, "daily") + "LORE").contentLore(player), "daily") : replace(player, Lang.DAILYUNAVAILABLELORE.contentLore(player), "daily")));
                } else {
                    cancel();
                }

            }}.runTaskTimer(plugin, 0, 20);
        inv.setItem(Integer.parseInt(Lang.WEEKLYPOSITION.content()), createGuiItem(Long.parseLong(cooldowns.getCooldown(player, "weekly", false)) < 0 ? Lang.WEEKLYAVAILABLEITEM.content(null).toUpperCase() : Lang.WEEKLYUNAVAILABLEITEM.content(null).toUpperCase(), Long.parseLong(cooldowns.getCooldown(player, "weekly", false)) < 0, Long.parseLong(cooldowns.getCooldown(player, "weekly", false)) < 0 ? Lang.WEEKLYDISPLAYNAMEAVAILABLE.content(player) : Lang.WEEKLYDISPLAYNAMEUNAVAILABLE.content(player), Long.parseLong(cooldowns.getCooldown(player, "weekly", false)) < 0 ? replace(player, Lang.valueOf("WEEKLYAVAILABLE" + plugin.getPremium(player, "weekly") + "LORE").contentLore(player), "weekly") : replace(player, Lang.WEEKLYUNAVAILABLELORE.contentLore(player), "weekly")));
        inv.setItem(Integer.parseInt(Lang.MONTHLYPOSITION.content()), createGuiItem(Long.parseLong(cooldowns.getCooldown(player, "monthly", false)) < 0 ? Lang.MONTHLYAVAILABLEITEM.content(null).toUpperCase() : Lang.MONTHLYUNAVAILABLEITEM.content(null).toUpperCase(), Long.parseLong(cooldowns.getCooldown(player, "monthly", false)) < 0, Long.parseLong(cooldowns.getCooldown(player, "monthly", false)) < 0 ? Lang.MONTHLYDISPLAYNAMEAVAILABLE.content(player) : Lang.MONTHLYDISPLAYNAMEUNAVAILABLE.content(player), Long.parseLong(cooldowns.getCooldown(player, "monthly", false)) < 0 ? replace(player, Lang.valueOf("MONTHLYAVAILABLE" + plugin.getPremium(player, "monthly") + "LORE").contentLore(player), "monthly") : replace(player, Lang.MONTHLYUNAVAILABLELORE.contentLore(player), "monthly")));
        return inv;
    }

    private List<String> replace(Player p, List<String> lore, String type){
        List<String> newLore = new ArrayList<>();
        for (String str : lore){
            newLore.add(str.replace("%cooldown%", cooldowns.getCooldown(p, type, true)));
        }
        return newLore;
    }

    private ItemStack createGuiItem(String id, boolean glow, String name, List<String> lore) {
        ItemStack item = new ItemStack(Objects.requireNonNull(Material.getMaterial(id)));
        ItemMeta meta = item.getItemMeta();
        if (id.equalsIgnoreCase("PLAYER_HEAD")) {
            meta = item.getItemMeta();
            if (meta != null) {
                ((SkullMeta) meta).setOwner(null);
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
