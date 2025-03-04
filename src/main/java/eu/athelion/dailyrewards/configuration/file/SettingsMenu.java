package eu.athelion.dailyrewards.configuration.file;

import eu.athelion.dailyrewards.DailyRewardsPlugin;
import eu.athelion.dailyrewards.configuration.TextModifier;
import eu.athelion.dailyrewards.configuration.YamlFile;
import eu.athelion.dailyrewards.manager.MenuManager;
import eu.athelion.dailyrewards.manager.Setting;
import eu.athelion.dailyrewards.user.User;
import eu.athelion.dailyrewards.util.ItemBuilder;
import eu.athelion.dailyrewards.util.Pair;
import eu.athelion.dailyrewards.util.PermissionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class SettingsMenu {

    private static final YamlFile configYamlFile = new YamlFile("menus/settings.yml",
            DailyRewardsPlugin.get().getDataFolder(), YamlFile.UpdateMethod.EVERYTIME);


    private static String title;
    private static Integer size;
    private static ItemStack filler = null;
    private static Pair<Integer, HashMap<SettingsStates, ItemBuilder>> join_notification, auto_claim = null;
    private static Pair<Integer, ItemBuilder> return_item;
    private static final HashMap<Integer, ItemBuilder> items = new HashMap<>();
    private static TextModifier textModifier;

    @Getter
    private static int AUTO_CLAIM_SLOT, JOIN_NOTIFICATION_SLOT, RETURN_ITEM_SLOT = 0;

    public static void reload() {
        configYamlFile.reload();
        textModifier = DailyRewardsPlugin.getTextModifier();
        items.clear();
        final YamlConfiguration cfg = configYamlFile.getConfiguration();
        title = cfg.getString("title");
        size = cfg.getInt("rows")*9;

        ConfigurationSection configSection = cfg.getConfigurationSection("items");
        for (String key : configSection.getKeys(false)) {
            ConfigurationSection section = configSection.getConfigurationSection(key);
            if (section == null) continue;
            if (section.getKeys(false).isEmpty()) continue;
            switch (key) {
                case "filler":
                    filler = readItem(section, false).getSecond().build();
                    break;
                case "join_notification":
                    join_notification = readSpecial(section);
                    break;
                case "auto_claim":
                    auto_claim = readSpecial(section);
                    break;
                case "return":
                    return_item = readItem(section, true);
                    break;
                default:
                    Pair<Integer, ItemBuilder> item = readItem(section, true);
                    items.put(item.getFirst(), item.getSecond());
                    break;
            }
        }
        AUTO_CLAIM_SLOT = auto_claim.getFirst();
        JOIN_NOTIFICATION_SLOT = join_notification.getFirst();
        RETURN_ITEM_SLOT = return_item.getFirst();
    }

    private static Pair<Integer, HashMap<SettingsStates, ItemBuilder>> readSpecial(ConfigurationSection section) {
        int slot = section.getInt("slot");
        HashMap<SettingsStates, ItemBuilder> statesHash = new HashMap<>();
        ConfigurationSection states = section.getConfigurationSection("states");
        for (String key : states.getKeys(false)) {
            statesHash.put(SettingsStates.valueOf(key.toUpperCase().replace("-", "_")), readItem(states.getConfigurationSection(key), false).getSecond());
        }
        return new Pair<>(slot, statesHash);
    }

    private static Pair<Integer, ItemBuilder> readItem(ConfigurationSection section, Boolean readSlot) {
        int slot = 0;
        if (readSlot) slot = section.getInt("slot");
        if (section.getString("material") == null) {
            DailyRewardsPlugin.get().consoleLog("&4Error occurred while creating item for settings menu! &cMissing material! ID/NAME: "+section.getName());
            return new Pair<>(slot, ItemBuilder.error());
        }
        ItemBuilder itemBuilder = ItemBuilder.from(section.getString("material")).guiBased();
        itemBuilder.setName(section.getString("display-name"));
        itemBuilder.setGlow(section.getBoolean("glow"));
        itemBuilder.setCustomModel(section.getInt("custom-model"));
        itemBuilder.setLore(section.getStringList("lore"));
        return new Pair<>(slot, itemBuilder);
    }

    private static final InventoryHolder SETTINGS_MENU_HOLDER = new MenuManager.RewardSettingsInventoryHolder();

    public static void render(User user) {
        if (join_notification == null || auto_claim == null) {
            if (user.isAdmin()) user.sendMessage("&4Can't render GUI for settings! &cCheck console!");
            else user.sendMessage("&4Can't render GUI for settings! &cReport to staff");
            DailyRewardsPlugin.get().consoleLog("&4Can't render GUI for settings! &cMissing join_notification or auto_claim");
            return;
        }

        Player player = user.getPlayer();

        final Inventory settings = Bukkit.createInventory(SETTINGS_MENU_HOLDER, size, textModifier.modifyText(player, title));

        if (filler != null)
            for (int i = 0; i < size; i++)
                settings.setItem(i, filler);

        if (!items.isEmpty())
            items.forEach((integer, itemBuilder) -> {
                settings.setItem(integer, itemBuilder.buildWPlaceholders(user, textModifier));
            });


        if (user.hasPermission(PermissionUtil.Permission.JOIN_NOTIFICATION_SETTING)) {
            if (user.hasSettingEnabled(Setting.JOIN_NOTIFICATION)) settings.setItem(join_notification.getFirst(), join_notification.getSecond().get(SettingsStates.ENABLED).buildWPlaceholders(user, textModifier));
            else settings.setItem(join_notification.getFirst(), join_notification.getSecond().get(SettingsStates.DISABLED).buildWPlaceholders(user, textModifier));
        } else settings.setItem(join_notification.getFirst(), join_notification.getSecond().get(SettingsStates.NO_PERMISSION).buildWPlaceholders(user, textModifier));


        if (user.hasPermission(PermissionUtil.Permission.AUTO_CLAIM_SETTING)) {
            if (user.hasSettingEnabled(Setting.AUTO_CLAIM)) settings.setItem(auto_claim.getFirst(), auto_claim.getSecond().get(SettingsStates.ENABLED).buildWPlaceholders(user, textModifier));
            else settings.setItem(auto_claim.getFirst(), auto_claim.getSecond().get(SettingsStates.DISABLED).buildWPlaceholders(user, textModifier));
        } else settings.setItem(auto_claim.getFirst(), auto_claim.getSecond().get(SettingsStates.NO_PERMISSION).buildWPlaceholders(user, textModifier));

        if (return_item != null)
            settings.setItem(return_item.getFirst(), return_item.getSecond().buildWPlaceholders(user, textModifier));

        player.openInventory(settings);
    }

}
