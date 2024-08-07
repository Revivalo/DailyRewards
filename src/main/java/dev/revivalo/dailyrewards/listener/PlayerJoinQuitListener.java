package dev.revivalo.dailyrewards.listener;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.api.event.ReminderReceiveEvent;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.configuration.file.Config;
import dev.revivalo.dailyrewards.configuration.file.Lang;
import dev.revivalo.dailyrewards.hook.Hook;
import dev.revivalo.dailyrewards.manager.Setting;
import dev.revivalo.dailyrewards.manager.reward.RewardType;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.util.PermissionUtil;
import dev.revivalo.dailyrewards.util.PlayerUtil;
import dev.revivalo.dailyrewards.util.VersionUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Set;

public class PlayerJoinQuitListener implements Listener {

    public static final PlayerJoinQuitListener instance = new PlayerJoinQuitListener();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        DataManager.loadPlayerDataAsync(player, data -> {

            User user = UserHandler.addUser(
                    new User(
                            player,
                            data
                    )
            );

            final Set<RewardType> availableRewards = user.getAvailableRewards();
            if (availableRewards.isEmpty()) {
                return;
            }

            if (!Hook.isAuthUsed()) {
                if (DailyRewardsPlugin.getRewardManager().processAutoClaimForUser(user)) {
                    return;
                }
            }

            if (!PermissionUtil.hasPermission(player, PermissionUtil.Permission.JOIN_NOTIFICATION_SETTING)) {
                return;
            }

            if (!user.hasSettingEnabled(Setting.JOIN_NOTIFICATION)) {
                return;
            }

            ReminderReceiveEvent reminderReceiveEvent = new ReminderReceiveEvent(player, availableRewards);
            Bukkit.getPluginManager().callEvent(reminderReceiveEvent);

            if (reminderReceiveEvent.isCancelled()) {
                return;
            }

            DailyRewardsPlugin.get().runDelayed(() -> {
                PlayerUtil.playSound(player, Config.JOIN_NOTIFICATION_SOUND.asString());
                for (String line : Lang.JOIN_NOTIFICATION.asReplacedList(new HashMap<String, String>() {{
                    put("%player%", player.getName());
                    put("%rewards%", String.valueOf(availableRewards.size()));
                }})) {
                    BaseComponent[] msg = TextComponent.fromLegacyText(line);

                    for (BaseComponent bc : msg) {
                        if (!VersionUtil.isLegacyVersion()) bc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Lang.JOIN_HOVER_MESSAGE.asColoredString(player))));
                        bc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Config.JOIN_NOTIFICATION_COMMAND.asString().replace("%player%", player.getName())));
                    }

                    player.spigot().sendMessage(msg);
                }

            }, Config.JOIN_NOTIFICATION_DELAY.asInt() * 20L);

        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onQuit(final PlayerQuitEvent event) {
        UserHandler.removeUser(event.getPlayer().getUniqueId());
    }

    public static PlayerJoinQuitListener getInstance() {
        return instance;
    }
}