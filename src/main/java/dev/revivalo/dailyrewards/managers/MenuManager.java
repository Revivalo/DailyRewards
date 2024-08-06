package dev.revivalo.dailyrewards.managers;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.Reward;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.ItemBuilder;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import dev.revivalo.dailyrewards.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class MenuManager {
    private ItemStack backgroundItem;

    private final InventoryHolder MAIN_MENU_HOLDER = new RewardsInventoryHolder();
    private final InventoryHolder SETTINGS_MENU_HOLDER = new RewardSettingsInventoryHolder();

    public MenuManager() {
        loadBackgroundFiller();
    }

    public void openRewardsMenu(final Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(DailyRewardsPlugin.get(), () -> {
            final int timer = 20;
            final Inventory inventory = Bukkit.createInventory(
                    MAIN_MENU_HOLDER,
                    Config.MENU_SIZE.asInt(),
                    TextUtils.applyPlaceholdersToString(player, Lang.MENU_TITLE.asColoredString(player)));

            if (Config.FILL_BACKGROUND_ENABLED.asBoolean()) {
                for (int i = 0; i < Config.MENU_SIZE.asInt(); i++)
                    inventory.setItem(i, backgroundItem);
            }

            final User user = UserHandler.getUser(player.getUniqueId());

            if (Config.SETTINGS_ENABLED_IN_MENU.asBoolean() && Config.SETTINGS_POSITION.asInt() < Config.MENU_SIZE.asInt())
                inventory.setItem(Config.SETTINGS_POSITION.asInt(), ItemBuilder.from(Config.SETTINGS_ITEM.asAnItem()).setName(Lang.SETTINGS_DISPLAY_NAME.asColoredString(player)).build());

            for (Reward reward : DailyRewardsPlugin.getRewardManager().getRewards()) {
                for (int slot : reward.getPosition()) {
                    user.getCooldownOfReward(reward.getRewardType()).thenAccept((cooldown -> {
                        final AtomicReference<BukkitTask> task = new AtomicReference<>();

                        task.set(Bukkit.getScheduler().runTaskTimer(DailyRewardsPlugin.get(), () -> {
                            if (!player.getOpenInventory().getTitle().equalsIgnoreCase(Lang.MENU_TITLE.asColoredString(player))) {
                                task.get().cancel();
                                return;
                            }

                            boolean premiumVariant = PermissionUtils.hasPremium(player, reward.getRewardType());
                            boolean claimable = cooldown.isClaimable();
                            inventory.setItem(slot,
                                    ItemBuilder.from(
                                                    claimable
                                                            ? reward.getAvailableItem()
                                                            : reward.getUnavailableItem()
                                            )
                                            .setGlow(claimable)
                                            .setName(
                                                    claimable
                                                            ? TextUtils.applyPlaceholdersToString(player, premiumVariant ? reward.getAvailablePremiumDisplayName() : reward.getAvailableDisplayName())
                                                            : TextUtils.applyPlaceholdersToString(player, reward.getUnavailableDisplayName())
                                            )
                                            .setLore(
                                                    claimable
                                                            ? TextUtils.applyPlaceholdersToList(player, premiumVariant ? reward.getAvailablePremiumLore() : reward.getAvailableLore())
                                                            : TextUtils.applyPlaceholdersToList(player, TextUtils.replaceList((premiumVariant ? reward.getUnavailablePremiumLore() : reward.getUnavailableLore()), new HashMap<String, String>() {{
                                                        put("cooldown", cooldown.getFormat(reward.getCooldownFormat()));
                                                    }}))
                                            )
                                            .build()
                            );

                        }, 0, timer));
                    }));
                }
            }

            player.openInventory(inventory);
        });
    }

    public void openSettings(final Player player) {
        if (!PermissionUtils.hasPermission(player, PermissionUtils.Permission.SETTINGS_MENU)) {
            player.sendMessage(Lang.PERMISSION_MSG.asColoredString(player));
            return;
        }

        final Inventory settings = Bukkit.createInventory(
                SETTINGS_MENU_HOLDER,
                Config.SETTINGS_MENU_SIZE.asInt(),
                Lang.SETTINGS_TITLE.asColoredString(player));

        if (Config.FILL_BACKGROUND_ENABLED.asBoolean()) {
            for (int i = 0; i < Config.SETTINGS_MENU_SIZE.asInt(); i++)
                settings.setItem(i, backgroundItem);
        }

        final User user = UserHandler.getUser(player.getUniqueId());

        settings.setItem(Config.SETTINGS_BACK_POSITION.asInt(), ItemBuilder.from(Config.SETTINGS_BACK_ITEM.asAnItem()).setName(Lang.BACK.asColoredString(player)).build());

        settings.setItem(Config.JOIN_NOTIFICATION_POSITION.asInt(), ItemBuilder.from(PermissionUtils.hasPermission(player, PermissionUtils.Permission.JOIN_NOTIFICATION_SETTING) ? Config.SETTINGS_JOIN_NOTIFICATION_ITEM.asAnItem() : new ItemStack(Material.BARRIER))
                .setName(
                        PermissionUtils.hasPermission(player, PermissionUtils.Permission.JOIN_NOTIFICATION_SETTING)
                                ? Lang.JOIN_NOTIFICATION_DISPLAY_NAME.asColoredString(player)
                                : Lang.NO_PERMISSION_SETTING_DISPLAY_NAME.asColoredString(player).replace("%settingType%", Lang.JOIN_NOTIFICATION_SETTING_NAME.asColoredString(player))
                )
                .setGlow(PermissionUtils.hasPermission(player, PermissionUtils.Permission.JOIN_NOTIFICATION_SETTING) && user.hasSettingEnabled(Setting.JOIN_NOTIFICATION))
                .setLore(
                        PermissionUtils.hasPermission(player, PermissionUtils.Permission.JOIN_NOTIFICATION_SETTING)
                                ? user.hasSettingEnabled(Setting.JOIN_NOTIFICATION)
                                ? Lang.JOIN_NOTIFICATION_ENABLED_LORE.asReplacedList(Collections.emptyMap())
                                : Lang.JOIN_NOTIFICATION_DISABLED_LORE.asReplacedList(Collections.emptyMap())
                                : Lang.NO_PERMISSION_SETTING_LORE.asReplacedList(new HashMap<String, String>() {{
                            put("%permission%", PermissionUtils.Permission.JOIN_NOTIFICATION_SETTING.get());
                        }})
                ).build()
        );

        settings.setItem(Config.AUTO_CLAIM_REWARDS_POSITION.asInt(), ItemBuilder.from(PermissionUtils.hasPermission(player, PermissionUtils.Permission.AUTO_CLAIM_SETTING) ? Config.SETTINGS_AUTO_CLAIM_ITEM.asAnItem() : new ItemStack(Material.BARRIER))
                .setName(PermissionUtils.hasPermission(player, PermissionUtils.Permission.AUTO_CLAIM_SETTING)
                        ? Lang.AUTO_CLAIM_DISPLAY_NAME.asColoredString(player)
                        : Lang.NO_PERMISSION_SETTING_DISPLAY_NAME.asColoredString(player).replace("%settingType%", Lang.JOIN_AUTO_CLAIM_SETTING_NAME.asColoredString(player)))
                .setGlow(PermissionUtils.hasPermission(player, PermissionUtils.Permission.AUTO_CLAIM_SETTING) && user.hasSettingEnabled(Setting.AUTO_CLAIM))
                .setLore(
                        PermissionUtils.hasPermission(player, PermissionUtils.Permission.AUTO_CLAIM_SETTING)
                                ? user.hasSettingEnabled(Setting.AUTO_CLAIM)
                                ? Lang.AUTO_CLAIM_ENABLED_LORE.asReplacedList(Collections.emptyMap())
                                : Lang.AUTO_CLAIM_DISABLED_LORE.asReplacedList(Collections.emptyMap())
                                : Lang.NO_PERMISSION_SETTING_LORE.asReplacedList(new HashMap<String, String>() {{
                            put("%permission%", PermissionUtils.Permission.AUTO_CLAIM_SETTING.get());
                        }})
                ).build()
        );

        player.openInventory(settings);
    }

    public void loadBackgroundFiller() {
        ItemBuilder.ItemBuilderBuilder backgroundItemBuilder = ItemBuilder.from(Config.BACKGROUND_ITEM.asAnItem());

        if (backgroundItemBuilder.getType() != Material.AIR) {
            backgroundItemBuilder.setName(" ");
        }
        backgroundItem = backgroundItemBuilder.build();
    }

    public static class RewardSettingsInventoryHolder implements InventoryHolder {

        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static class RewardsInventoryHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
