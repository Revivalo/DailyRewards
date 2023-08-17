package dev.revivalo.dailyrewards.configuration.enums;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Splitter;
import dev.dbassett.skullcreator.SkullCreator;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.YamlFile;
import dev.revivalo.dailyrewards.hooks.Hooks;
import dev.revivalo.dailyrewards.utils.TextUtils;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public enum Config {
    MENU_SIZE("menu-size"),
    FILL_BACKGROUND("fill-background-enabled"),
    BACKGROUND_ITEM("background-item"),
    JOIN_NOTIFICATION_BY_DEFAULT("join-notification-by-default"),
    JOIN_NOTIFICATION_COMMAND("join-notification-command"),
    ANNOUNCE_ENABLED("announce-enabled"),
    JOIN_NOTIFICATION_DELAY("join-notification-delay"),
    UNAVAILABLE_REWARD_SOUND("unavailable-reward-sound"),
    AUTO_CLAIM_REWARDS_ON_JOIN_BY_DEFAULT("auto-claim-rewards-on-join-by-default"),
    AUTO_CLAIM_REWARDS_POSITION("auto-claim-rewards-position"),
    JOIN_NOTIFICATION_POSITION("join-notification-position"),
    JOIN_NOTIFICATION_SOUND("join-notification-sound"),
    DISABLED_WORLDS("disabled-worlds"),
    FIRST_TIME_REQUIRED_PLAY_TIME("first-time-join-required-play-time"),

    SETTINGS_ENABLED_IN_MENU("settings-enabled-in-menu"),
    SETTINGS_POSITION("settings-position"),
    SETTINGS_ITEM("settings-item"),
    SETTINGS_JOIN_NOTIFICATION_ITEM("settings-join-notification-item"),
    SETTINGS_AUTO_CLAIM_ITEM("settings-auto-claim-item"),
    SETTINGS_BACK_ITEM("settings-back-item"),
    SETTINGS_BACK_POSITION("settings-back-position"),

    USE_MYSQL("use-mysql"),
    MYSQL_IP("mysql-ip"),
    MYSQL_PORT("mysql-port"),
    MYSQL_DBNAME("mysql-database-name"),
    MYSQL_USERNAME("mysql-username"),
    MYSQL_PASSWORD("mysql-password"),
    UPDATE_CHECKER("update-checker"),

    DAILY_ENABLED("daily-enabled"),
    DAILY_COOLDOWN("daily-cooldown"),
    DAILY_COOLDOWN_FORMAT("daily-cooldown-format"),
    DAILY_AVAILABLE_AFTER_FIRST_JOIN("daily-available-after-first-join"),
    DAILY_PLACEHOLDER("daily-placeholder"),
    DAILY_POSITION("daily-position"),
    DAILY_SOUND("daily-sound"),
    DAILY_AVAILABLE_ITEM("daily-available-item"),
    DAILY_UNAVAILABLE_ITEM("daily-unavailable-item"),
    DAILY_REWARDS("daily-rewards"),
    DAILY_PREMIUM_REWARDS("daily-premium-rewards"),

    WEEKLY_ENABLED("weekly-enabled"),
    WEEKLY_COOLDOWN("weekly-cooldown"),
    WEEKLY_COOLDOWN_FORMAT("weekly-cooldown-format"),
    WEEKLY_AVAILABLE_AFTER_FIRST_JOIN("weekly-available-after-first-join"),
    WEEKLY_PLACEHOLDER("weekly-placeholder"),
    WEEKLY_AVAILABLE_ITEM("weekly-available-item"),
    WEEKLY_UNAVAILABLE_ITEM("weekly-unavailable-item"),
    WEEKLY_POSITION("weekly-position"),
    WEEKLY_SOUND("weekly-sound"),
    WEEKLY_REWARDS("weekly-rewards"),
    WEEKLY_PREMIUM_REWARDS("weekly-premium-rewards"),

    MONTHLY_ENABLED("monthly-enabled"),
    MONTHLY_COOLDOWN("monthly-cooldown"),
    MONTHLY_COOLDOWN_FORMAT("monthly-cooldown-format"),
    MONTHLY_AVAILABLE_AFTER_FIRST_JOIN("monthly-available-after-first-join"),
    MONTHLY_PLACEHOLDER("monthly-placeholder"),
    MONTHLY_AVAILABLE_ITEM("monthly-available-item"),
    MONTHLY_UNAVAILABLE_ITEM("monthly-unavailable-item"),
    MONTHLY_POSITION("monthly-position"),
    MONTHLY_SOUND("monthly-sound"),
    MONTHLY_REWARDS("monthly-rewards"),
    MONTHLY_PREMIUM_REWARDS("monthly-premium-rewards"),
    CHECK_FOR_FULL_INVENTORY("check-for-full-inventory");

    private static final YamlFile configYamlFile = new YamlFile("config.yml",
            DailyRewardsPlugin.get().getDataFolder(), YamlFile.UpdateMethod.EVERYTIME);
    private static final Map<String, String> messages = new HashMap<>();
    private static final Map<String, String> listsStoredAsStrings = new HashMap<>();
    private static final Map<String, ItemStack> items = new HashMap<>();

    private final String text;

    Config(String text) {
        this.text = text;
    }

    public static void reload() {
        configYamlFile.reload();
        final YamlConfiguration configuration = configYamlFile.getConfiguration();

        final ConfigurationSection configurationSection = configuration.getConfigurationSection("config");
        Objects.requireNonNull(configurationSection)
                .getKeys(false)
                .forEach(key -> {
                    if (key.endsWith("lore") || key.endsWith("rewards") || key.endsWith("notifications") || key.endsWith("help") || key.endsWith("worlds")) {
                        listsStoredAsStrings.put(key, String.join("⎶", configurationSection.getStringList(key)));
                    } else messages.put(key, configurationSection.getString(key));
                });

        loadItems(configurationSection);

        Lang.reload();
    }


    public static void loadItems(ConfigurationSection configurationSection) {
        configurationSection
                .getKeys(false)
                .forEach(key -> {
                    if (key.endsWith("item")) {
                        final String itemName = configurationSection.getString(key);
                        if (itemName.length() > 64) {
                            items.put(key, SkullCreator.itemFromBase64(itemName));
                        } else if (itemName.startsWith("CustomModel")) {
                            Material material = Material.valueOf(TextUtils.getPlaceholders(itemName, "[").stream().findFirst().get().replace("[", "").replace("]", "").toUpperCase(Locale.ENGLISH));
                            int data = Integer.parseInt(TextUtils.getPlaceholders(itemName, "{").stream().findFirst().get().replace("{", "").replace("}", ""));
                            final ItemStack itemStack = new ItemStack(material);
                            final ItemMeta meta = itemStack.getItemMeta();
                            meta.setCustomModelData(data);
                            itemStack.setItemMeta(meta);
                            items.put(key, itemStack);
                        } else if (Hooks.getItemsAdderHook().isOn() && ItemsAdder.isCustomItem(itemName)) {
                            items.put(key, CustomStack.getInstance(itemName).getItemStack());
                        } else if (Hooks.getOraxenHook().isOn() && OraxenItems.exists(itemName)) {
                            items.put(key, OraxenItems.getItemById(itemName).build());
                        } else {
                            items.put(key, XMaterial.matchXMaterial(itemName.toUpperCase(Locale.ENGLISH)).orElse(XMaterial.STONE).parseItem());
                        }
                    }
                });
    }

    public List<String> asReplacedList(final Map<String, String> definitions) {
        return Splitter.on("⎶").splitToList(TextUtils.replaceString(listsStoredAsStrings.get(this.text), definitions));
    }

    public String asString() {
        return messages.get(text);
    }

    public ItemStack asAnItem() {
        return items.get(this.text);
    }

    public String asUppercase() {
        return this.asString().toUpperCase();
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(asString());
    }

    public long asLong() {
        return Long.parseLong(asString()) * 3600000;
    }

    public int asInt() {
        return Integer.parseInt(asString());
    }
}
