package eu.athelion.dailyrewards.manager;

import eu.athelion.dailyrewards.DailyRewardsPlugin;
import eu.athelion.dailyrewards.configuration.file.Config;
import eu.athelion.dailyrewards.configuration.file.Lang;
import eu.athelion.dailyrewards.configuration.file.SettingsMenu;
import eu.athelion.dailyrewards.manager.cooldown.Cooldown;
import eu.athelion.dailyrewards.manager.reward.Reward;
import eu.athelion.dailyrewards.manager.reward.RewardType;
import eu.athelion.dailyrewards.manager.reward.action.ClaimAction;
import eu.athelion.dailyrewards.user.User;
import eu.athelion.dailyrewards.user.UserHandler;
import eu.athelion.dailyrewards.util.ItemBuilder;
import eu.athelion.dailyrewards.util.PermissionUtil;
import eu.athelion.dailyrewards.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class MenuManager implements Listener {
    private ItemStack backgroundItem;

    private final InventoryHolder MAIN_MENU_HOLDER = new RewardsInventoryHolder();

    public MenuManager() {
        DailyRewardsPlugin.get().registerListeners(this);
        loadBackgroundFiller();
    }

    public void openRewardsMenu(final Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(DailyRewardsPlugin.get(), () -> {
            final int timer = 20;
            final Inventory inventory = Bukkit.createInventory(
                    MAIN_MENU_HOLDER,
                    Config.MENU_SIZE.asInt(),
                    TextUtil.applyPlaceholdersToString(player, Lang.MENU_TITLE.asColoredString(player)));

            if (Config.FILL_BACKGROUND_ENABLED.asBoolean()) {
                for (int i = 0; i < Config.MENU_SIZE.asInt(); i++)
                    inventory.setItem(i, backgroundItem);
            }

            final User user = UserHandler.getUser(player.getUniqueId());

            if (Config.SETTINGS_ENABLED_IN_MENU.asBoolean() && Config.SETTINGS_POSITION.asInt() < Config.MENU_SIZE.asInt())
                inventory.setItem(Config.SETTINGS_POSITION.asInt(), ItemBuilder.from(Config.SETTINGS_ITEM.asAnItem()).setName(Lang.SETTINGS_DISPLAY_NAME.asColoredString(player)).build());

            for (Reward reward : DailyRewardsPlugin.getRewardManager().getRewards()) {
                Cooldown cooldown = user.getCooldown(reward.getType());
                for (int slot : reward.getPosition()) {

                    final AtomicReference<BukkitTask> task = new AtomicReference<>();
                    task.set(Bukkit.getScheduler().runTaskTimer(DailyRewardsPlugin.get(), () -> {
                        if (!player.getOpenInventory().getTitle().equalsIgnoreCase(Lang.MENU_TITLE.asColoredString(player))) {
                            task.get().cancel();
                            return;
                        }

                        boolean premiumVariant = PermissionUtil.hasPremium(player, reward.getType());
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
                                                        ? TextUtil.applyPlaceholdersToString(player, premiumVariant ? reward.getAvailablePremiumDisplayName() : reward.getAvailableDisplayName())
                                                        : TextUtil.applyPlaceholdersToString(player, reward.getUnavailableDisplayName())
                                        )
                                        .setLore(
                                                claimable
                                                        ? TextUtil.applyPlaceholdersToList(player, premiumVariant ? reward.getAvailablePremiumLore() : reward.getAvailableLore())
                                                        : TextUtil.applyPlaceholdersToList(player, TextUtil.replaceList((premiumVariant ? reward.getUnavailablePremiumLore() : reward.getUnavailableLore()), new HashMap<String, String>() {{
                                                    put("cooldown", cooldown.getFormat(reward.getCooldownFormat()));
                                                }}))
                                        )
                                        .build()
                        );

                    }, 0, timer));
                }
            }

            player.openInventory(inventory);
        });
    }

    public void openSettings(final Player player) {
        if (!PermissionUtil.hasPermission(player, PermissionUtil.Permission.SETTINGS_MENU)) {
            player.sendMessage(Lang.INSUFFICIENT_PERMISSION.asColoredString(player)
                    .replace("%permission%", PermissionUtil.Permission.SETTINGS_MENU.get()));
            return;
        }
        final User user = UserHandler.getUser(player.getUniqueId());
        SettingsMenu.render(user);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof MenuManager.RewardsInventoryHolder)) return;
        if (event.getCurrentItem() == null) return;
        event.setCancelled(true);
        final Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        if (Config.DAILY_POSITIONS.asIntegerList().contains(slot)) {
            new ClaimAction(player).preCheck(player, RewardType.DAILY);
        } else if (Config.WEEKLY_POSITIONS.asIntegerList().contains(slot)) {
            new ClaimAction(player).preCheck(player, RewardType.WEEKLY);
        } else if (Config.MONTHLY_POSITIONS.asIntegerList().contains(slot)) {
            new ClaimAction(player).preCheck(player, RewardType.MONTHLY);
        } else if (slot == Config.SETTINGS_POSITION.asInt()) {
            DailyRewardsPlugin.getMenuManager().openSettings(player);
        }
    }

    @EventHandler
    public void inventoryClick(final InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof MenuManager.RewardSettingsInventoryHolder)) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        event.setCancelled(true);

        final User user = UserHandler.getUser(event.getWhoClicked().getUniqueId());
        final Player player = user.getPlayer();

        int slot = event.getSlot();
        if (slot == SettingsMenu.getJOIN_NOTIFICATION_SLOT()) {
            if (!PermissionUtil.hasPermission(player, PermissionUtil.Permission.JOIN_NOTIFICATION_SETTING)) {
                player.sendMessage(Lang.INSUFFICIENT_PERMISSION.asColoredString(player)
                        .replace("%permission%", PermissionUtil.Permission.JOIN_NOTIFICATION_SETTING.get()));
                return;
            }

            user.toggleSetting(Setting.JOIN_NOTIFICATION, !user.hasSettingEnabled(Setting.JOIN_NOTIFICATION));

            DailyRewardsPlugin.getMenuManager().openSettings(user.getPlayer());
        } else if (slot == SettingsMenu.getAUTO_CLAIM_SLOT()) {
            if (!PermissionUtil.hasPermission(player, PermissionUtil.Permission.AUTO_CLAIM_SETTING)) {
                player.sendMessage(Lang.INSUFFICIENT_PERMISSION.asColoredString(player));
                return;
            }

            user.toggleSetting(Setting.AUTO_CLAIM, !user.hasSettingEnabled(Setting.AUTO_CLAIM));

            DailyRewardsPlugin.getMenuManager().openSettings(user.getPlayer());
        } else if (slot == Config.SETTINGS_POSITION.asInt()) {
            DailyRewardsPlugin.getMenuManager().openRewardsMenu(user.getPlayer());
        }
    }

    public void loadBackgroundFiller() {
        ItemBuilder backgroundItemBuilder = ItemBuilder.from(Config.BACKGROUND_ITEM.asAnItem());

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
