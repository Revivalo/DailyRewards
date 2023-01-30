package cz.revivalo.dailyrewards.utils;

import lombok.NonNull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class ItemBuilder {
    public static ItemBuilderBuilder from(@NonNull ItemStack itemStack) {
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

        public ItemBuilderBuilder setAmount(final int amount) {
            this.itemStack.setAmount(amount);
            return this;
        }

        public ItemBuilderBuilder setGlow(final boolean glow) {
            if (glow) {
                Objects.requireNonNull(this.meta).addEnchant(Enchantment.LURE, 1, false);
                this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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
    }
}
