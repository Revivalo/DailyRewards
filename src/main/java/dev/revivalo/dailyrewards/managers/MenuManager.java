package dev.revivalo.dailyrewards.managers;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.Reward;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.ItemBuilder;
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
                    TextUtils.applyPlaceholdersToString(player, Lang.MENU_TITLE.asColoredString()));

            if (Config.FILL_BACKGROUND.asBoolean()) {
                for (int i = 0; i < Config.MENU_SIZE.asInt(); i++)
                    inventory.setItem(i, backgroundItem);
            }

            final User user = UserHandler.getUser(player.getUniqueId());

            if (Config.SETTINGS_ENABLED_IN_MENU.asBoolean() && Config.SETTINGS_POSITION.asInt() < Config.MENU_SIZE.asInt())
                inventory.setItem(Config.SETTINGS_POSITION.asInt(), ItemBuilder.from(Config.SETTINGS_ITEM.asAnItem()).setName(Lang.SETTINGS_DISPLAY_NAME.asColoredString()).build());

            for (Reward reward : DailyRewardsPlugin.getRewardManager().getRewards()) {
                user.getCooldownOfReward(reward.getRewardType()).thenAccept((cooldown -> {
                    final AtomicReference<BukkitTask> task = new AtomicReference<>();

                    task.set(Bukkit.getScheduler().runTaskTimer(DailyRewardsPlugin.get(), () -> {
                        if (!player.getOpenInventory().getTitle().equalsIgnoreCase(Lang.MENU_TITLE.asColoredString())) {
                            task.get().cancel();
                            return;
                        }

                        boolean claimable = cooldown.isClaimable();
                        inventory.setItem(reward.getPosition(),
                                ItemBuilder.from(
                                                claimable
                                                        ? reward.getAvailableItem()
                                                        : reward.getUnavailableItem()
                                        )
                                        .setGlow(claimable)
                                        .setName(
                                                claimable
                                                        ? TextUtils.applyPlaceholdersToString(player, DailyRewardsPlugin.isPremium(player, reward.getRewardType()) ? reward.getAvailablePremiumDisplayName() : reward.getAvailableDisplayName())
                                                        : TextUtils.applyPlaceholdersToString(player, reward.getUnavailableDisplayName())
                                        )
                                        .setLore(
                                                claimable
                                                        ? TextUtils.applyPlaceholdersToList(player, DailyRewardsPlugin.isPremium(player, reward.getRewardType()) ? reward.getAvailablePremiumLore() : reward.getAvailableLore())
                                                        : TextUtils.applyPlaceholdersToList(player, TextUtils.replaceList((DailyRewardsPlugin.isPremium(player, reward.getRewardType()) ? reward.getUnavailablePremiumLore() : reward.getUnavailableLore()), new HashMap<String, String>() {{
                                                                put("cooldown", cooldown.getFormat(reward.getCooldownFormat()));
                                                            }}))
                                        )
                                        .build()
                        );

                    }, 0, timer));
                }));
            }

            player.openInventory(inventory);
        });
    }

    public void openSettings(final Player player) {
        if (!player.hasPermission("dailyreward.settings")) {
            player.sendMessage(Lang.PERMISSION_MESSAGE.asColoredString());
            return;
        }

        final Inventory settings = Bukkit.createInventory(
                SETTINGS_MENU_HOLDER,
                Config.SETTINGS_MENU_SIZE.asInt(),
                Lang.SETTINGS_TITLE.asColoredString());

        if (Config.FILL_BACKGROUND.asBoolean()) {
            for (int i = 0; i < Config.SETTINGS_MENU_SIZE.asInt(); i++)
                settings.setItem(i, backgroundItem);
        }

        final User user = UserHandler.getUser(player.getUniqueId());

        settings.setItem(Config.SETTINGS_BACK_POSITION.asInt(), ItemBuilder.from(Config.SETTINGS_BACK_ITEM.asAnItem()).setName(Lang.BACK.asColoredString()).build());

        settings.setItem(Config.JOIN_NOTIFICATION_POSITION.asInt(), ItemBuilder.from(Config.SETTINGS_JOIN_NOTIFICATION_ITEM.asAnItem())
                .setName(Lang.JOIN_NOTIFICATION_DISPLAY_NAME.asColoredString())
                .setGlow(user.hasEnabledJoinNotification())
                .setLore(
                        user.hasEnabledJoinNotification()
                                ? Lang.JOIN_NOTIFICATION_ENABLED_LORE.asReplacedList(Collections.emptyMap())
                                : Lang.JOIN_NOTIFICATION_DISABLED_LORE.asReplacedList(Collections.emptyMap())
                ).build()
        );

        settings.setItem(Config.AUTO_CLAIM_REWARDS_POSITION.asInt(), ItemBuilder.from(new ItemStack(Config.SETTINGS_AUTO_CLAIM_ITEM.asAnItem()))
                .setName(Lang.AUTO_CLAIM_DISPLAY_NAME.asColoredString())
                .setGlow(user.hasEnabledAutoClaim())
                .setLore(
                        user.hasEnabledAutoClaim()
                                ? Lang.AUTO_CLAIM_ENABLED_LORE.asReplacedList(Collections.emptyMap())
                                : Lang.AUTO_CLAIM_DISABLED_LORE.asReplacedList(Collections.emptyMap())
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
