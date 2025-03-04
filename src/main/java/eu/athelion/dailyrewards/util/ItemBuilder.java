package eu.athelion.dailyrewards.util;

import com.cryptomorin.xseries.XMaterial;
import eu.athelion.dailyrewards.configuration.TextModifier;
import eu.athelion.dailyrewards.user.User;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemBuilder {
    public static ItemBuilder from(@NotNull ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }
    public static ItemBuilder from(String material) {
        Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(material);
        return xMaterial.map(value -> {
            assert value.parseMaterial() != null;
            return new ItemBuilder(new ItemStack(value.parseMaterial()));
        }).orElseGet(() -> new ItemBuilder(new ItemStack(Material.BARRIER)));
    }

    public static ItemBuilder error() {
        return new ItemBuilder(new ItemStack(Material.BARRIER)).setName("&cError");
    }

    private final ItemStack itemStack;
    private final ItemMeta meta;
    private String displayName = null;
    private List<String> lore = new ArrayList<>();
    private Integer customModel = -1;
    private List<ItemFlag> itemFlags = new ArrayList<>();
    private Boolean glow = false;

    private ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.meta = itemStack.getItemMeta();
    }

    public ItemBuilder setName(final String name) {
        this.displayName = name;
        return this;
    }

    public ItemBuilder setCustomModel(final Integer data) {
        this.customModel = data;
        return this;
    }

    public ItemBuilder setItemFlags(ItemFlag... itemFlags) {
        this.itemFlags.addAll(Arrays.asList(itemFlags));
        return this;
    }

    public ItemBuilder guiBased() {
        this.itemFlags.addAll(Arrays.asList(ItemFlag.values()));
        return this;
    }

    public ItemBuilder setGlow(final boolean glow) {
        this.glow = glow;
        return this;
    }

    public ItemBuilder setLore(final List<String> lore) {
        this.lore.addAll(lore);
        return this;
    }

    private void baseBuild() {
        if (this.glow) {
            Objects.requireNonNull(this.meta).addEnchant(Enchantment.LURE, 1, false);
            this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else this.meta.getEnchants().forEach((enchant, level) -> this.meta.removeEnchant(enchant));
        if (!this.itemFlags.isEmpty()) this.meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        if (this.customModel > -1) this.meta.setCustomModelData(this.customModel);
    }

    public ItemStack build() {
        baseBuild();
        if (this.displayName != null) this.meta.setDisplayName(TextUtil.colorize(this.displayName));
        if (!this.lore.isEmpty()) this.meta.setLore(TextUtil.colorize(lore));
        this.itemStack.setItemMeta(this.meta);
        return this.itemStack;
    }

    public ItemStack buildWPlaceholders(User user, TextModifier modifier) {
        baseBuild();
        if (this.displayName != null) this.meta.setDisplayName(modifier.modifyText(user.getPlayer(), displayName));
        if (!this.lore.isEmpty()) this.meta.setLore(modifier.modifyList(user.getPlayer(), lore));
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
