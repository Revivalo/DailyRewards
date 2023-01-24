package cz.revivalo.dailyrewards.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class ItemBuilder {
    private final ItemStack itemStack;
    private final ItemMeta meta;
    public ItemBuilder(final ItemStack itemStack){
        this.itemStack = itemStack;
        this.meta = itemStack.getItemMeta();
    }

    public ItemBuilder setName(final String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setAmount(final int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setGlow(final boolean glow) {
        if (glow) {
            Objects.requireNonNull(meta).addEnchant(Enchantment.LURE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder setLore(final List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
