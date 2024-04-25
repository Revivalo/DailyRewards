package dev.revivalo.dailyrewards.configuration.enums;

import com.cryptomorin.xseries.XMaterial;
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
import java.util.stream.Collectors;

public enum Config {
    MENU_SIZE("menu-size"),
    SETTINGS_MENU_SIZE("settings-menu-size"),
    FILL_BACKGROUND("fill-background-enabled"),
    BACKGROUND_ITEM("background-item"),
    OPEN_MENU_AFTER_CLAIMING("open-menu-after-claiming"),
    JOIN_NOTIFICATION_BY_DEFAULT("join-notification-by-default"),
    JOIN_NOTIFICATION_COMMAND("join-notification-command"),
    ANNOUNCE_ENABLED("announce-enabled"),
    JOIN_NOTIFICATION_DELAY("join-notification-delay"),
    JOIN_AUTO_CLAIM_DELAY("join-auto-claim-delay"),
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

    BACKEND("backend"),
    MYSQL_IP("mysql-ip"),
    MYSQL_PORT("mysql-port"),
    MYSQL_DBNAME("mysql-database-name"),
    MYSQL_USERNAME("mysql-username"),
    MYSQL_PASSWORD("mysql-password"),
    UPDATE_CHECKER("update-checker"),
    MYSQL_POOL_SETTINGS_MAXIMUM_POOL_SIZE("mysql-pool-settings-maximum-pool-size"),
    MYSQL_POOL_SETTINGS_MINIMUM_IDLE("mysql-pool-settings-minimum-idle"),
    MYSQL_POOL_SETTINGS_MAXIMUM_LIFETIME("mysql-pool-settings-maximum-lifetime"),
    MYSQL_POOL_SETTINGS_CONNECTION_TIMEOUT("mysql-pool-settings-connection-timeout"),

    MYSQL_PROPERTIES("mysql-properties"),

    DAILY_ENABLED("daily-enabled"),
    DAILY_COOLDOWN("daily-cooldown"),
    DAILY_COOLDOWN_FORMAT("daily-cooldown-format"),
    DAILY_AVAILABLE_AFTER_FIRST_JOIN("daily-available-after-first-join"),
    DAILY_PLACEHOLDER("daily-placeholder"),
    DAILY_POSITIONS("daily-positions"),
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
    WEEKLY_POSITIONS("weekly-positions"),
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
    MONTHLY_POSITIONS("monthly-positions"),
    MONTHLY_SOUND("monthly-sound"),
    MONTHLY_REWARDS("monthly-rewards"),
    MONTHLY_PREMIUM_REWARDS("monthly-premium-rewards"),
    CHECK_FOR_FULL_INVENTORY("check-for-full-inventory");

    private static final YamlFile configYamlFile = new YamlFile("config.yml",
            DailyRewardsPlugin.get().getDataFolder(), YamlFile.UpdateMethod.EVERYTIME);
    private static final Map<String, String> strings = new HashMap<>();
    private static final Map<String, List<String>> lists = new HashMap<>();
    private static final Map<String, ItemStack> items = new HashMap<>();

    private final String text;

    Config(String text) {
        this.text = text;
    }

    public static void reload() {
        configYamlFile.reload();
        final YamlConfiguration configuration = configYamlFile.getConfiguration();

        ConfigurationSection configSection = configuration.getConfigurationSection("config");

        configSection
                .getKeys(false)
                .forEach(key -> {
                    if (configSection.isList(key)) {
                        lists.put(key, configSection.getStringList(key));
                    } else
                        strings.put(key, configSection.getString(key));
                });

        loadItems(configSection);

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

    public Map<String, String> asStringMap() {
        Map<String, String> map = new HashMap<>();
        ConfigurationSection section = configYamlFile.getConfiguration().getConfigurationSection(asString());
        if (section == null) return map;
        for (String key : section.getKeys(false)) {
            map.put(key, section.getString(key));
        }
        return map;
    }

    public List<String> asReplacedList(Map<String, String> definitions) {
        return lists.get(text);
    }

    public String asString() {
        return strings.get(text);
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
        return Long.parseLong(asString());
    }

    public int asInt() {
        return Integer.parseInt(asString());
    }

    public List<Integer> asIntegerList() {
        return asReplacedList(Collections.emptyMap()).stream().map(Integer::parseInt).collect(Collectors.toList());
    }
}
