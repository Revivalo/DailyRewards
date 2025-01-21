package eu.athelion.dailyrewards.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ItemBuilder {
    public static ItemBuilderBuilder from(@NotNull ItemStack itemStack) {
        return new ItemBuilderBuilder(itemStack);
    }

    public static class ItemBuilderBuilder {
        private final ItemStack itemStack;
        private final ItemMeta meta;


        ItemBuilderBuilder(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.meta = itemStack.getItemMeta();
        }

        public ItemBuilderBuilder setName(final String name) {
            this.meta.setDisplayName(name);
            return this;
        }

        public ItemBuilderBuilder setItemFlags(ItemFlag... itemFlags) {
            this.meta.addItemFlags(itemFlags);
            return this;
        }

        public ItemBuilderBuilder setGlow(final boolean glow) {
            if (glow) {
                Objects.requireNonNull(this.meta).addEnchant(Enchantment.LURE, 1, false);
                this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                this.meta.getEnchants().forEach((enchant, level) -> this.meta.removeEnchant(enchant));
            }
            return this;
        }

        public ItemBuilderBuilder setLore(final List<String> lore) {
            this.meta.setLore(lore);
            return this;
        }

        public ItemStack build() {
            this.itemStack.setItemMeta(this.meta);
            return this.itemStack;
        }

        public String toString() {
            return "ItemBuilder.ItemBuilderBuilder(itemStack=" + this.itemStack + ", meta=" + this.meta + ")";
        }

        public Material getType() {
            return this.itemStack.getType();
        }
    }
}
