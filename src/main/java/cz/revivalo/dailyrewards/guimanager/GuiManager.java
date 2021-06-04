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

    public Inventory openRewardsMenu(Player p){
        Inventory inv = Bukkit.createInventory(new Rewards(), Integer.parseInt(Lang.MENUSIZE.content(p)), Lang.MENUTITLE.content(p));
        if (Boolean.parseBoolean(Lang.FILLBACKGROUND.content(p))){
            for (int i = 0; i < Integer.parseInt(Lang.MENUSIZE.content(p)); i++){
                inv.setItem(i, createGuiItem(Lang.BACKGROUNDITEM.content(p).toUpperCase(), false, "", null));
            }
        }
        inv.setItem(Integer.parseInt(Lang.DAILYPOSITION.content(null)), createGuiItem(Long.parseLong(cooldowns.getCooldown(p, "daily", false)) < 0 ? Lang.DAILYAVAILABLEITEM.content(null) : Lang.DAILYUNAVAILABLEITEM.content(null).toUpperCase(), Long.parseLong(cooldowns.getCooldown(p, "daily", false)) < 0, Long.parseLong(cooldowns.getCooldown(p, "daily", false)) < 0 ? Lang.DAILYDISPLAYNAMEAVAILABLE.content(p) : Lang.DAILYDISPLAYNAMEUNAVAILABLE.content(p), Long.parseLong(cooldowns.getCooldown(p, "daily", false)) < 0 ? replace(p, Lang.valueOf("DAILYAVAILABLE" + plugin.getPremium(p, "daily") + "LORE").contentLore(p), "daily") : replace(p, Lang.DAILYUNAVAILABLELORE.contentLore(p), "daily")));
        new BukkitRunnable() {

            @Override
            public void run() {
                if (p.getOpenInventory().getTitle().equalsIgnoreCase(Lang.MENUTITLE.content(p))) {
                    inv.setItem(Integer.parseInt(Lang.DAILYPOSITION.content(null)), createGuiItem(Long.parseLong(cooldowns.getCooldown(p, "daily", false)) < 0 ? Lang.DAILYAVAILABLEITEM.content(null) : Lang.DAILYUNAVAILABLEITEM.content(null).toUpperCase(), Long.parseLong(cooldowns.getCooldown(p, "daily", false)) < 0, Long.parseLong(cooldowns.getCooldown(p, "daily", false)) < 0 ? Lang.DAILYDISPLAYNAMEAVAILABLE.content(p) : Lang.DAILYDISPLAYNAMEUNAVAILABLE.content(p), Long.parseLong(cooldowns.getCooldown(p, "daily", false)) < 0 ? replace(p, Lang.valueOf("DAILYAVAILABLE" + plugin.getPremium(p, "daily") + "LORE").contentLore(p), "daily") : replace(p, Lang.DAILYUNAVAILABLELORE.contentLore(p), "daily")));
                } else {
                    cancel();
                }

            }}.runTaskTimer(plugin, 0, 20);
        inv.setItem(Integer.parseInt(Lang.WEEKLYPOSITION.content(null)), createGuiItem(Long.parseLong(cooldowns.getCooldown(p, "weekly", false)) < 0 ? Lang.WEEKLYAVAILABLEITEM.content(null).toUpperCase() : Lang.WEEKLYUNAVAILABLEITEM.content(null).toUpperCase(), Long.parseLong(cooldowns.getCooldown(p, "weekly", false)) < 0, Long.parseLong(cooldowns.getCooldown(p, "weekly", false)) < 0 ? Lang.WEEKLYDISPLAYNAMEAVAILABLE.content(p) : Lang.WEEKLYDISPLAYNAMEUNAVAILABLE.content(p), Long.parseLong(cooldowns.getCooldown(p, "weekly", false)) < 0 ? replace(p, Lang.valueOf("WEEKLYAVAILABLE" + plugin.getPremium(p, "weekly") + "LORE").contentLore(p), "weekly") : replace(p, Lang.WEEKLYUNAVAILABLELORE.contentLore(p), "weekly")));
        inv.setItem(Integer.parseInt(Lang.MONTHLYPOSITION.content(null)), createGuiItem(Long.parseLong(cooldowns.getCooldown(p, "monthly", false)) < 0 ? Lang.MONTHLYAVAILABLEITEM.content(null).toUpperCase() : Lang.MONTHLYUNAVAILABLEITEM.content(null).toUpperCase(), Long.parseLong(cooldowns.getCooldown(p, "monthly", false)) < 0, Long.parseLong(cooldowns.getCooldown(p, "monthly", false)) < 0 ? Lang.MONTHLYDISPLAYNAMEAVAILABLE.content(p) : Lang.MONTHLYDISPLAYNAMEUNAVAILABLE.content(p), Long.parseLong(cooldowns.getCooldown(p, "monthly", false)) < 0 ? replace(p, Lang.valueOf("MONTHLYAVAILABLE" + plugin.getPremium(p, "monthly") + "LORE").contentLore(p), "monthly") : replace(p, Lang.MONTHLYUNAVAILABLELORE.contentLore(p), "monthly")));
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
        }
        Objects.requireNonNull(meta).setLore(lore);
        item.setItemMeta(meta);

        return item;
    }
}
