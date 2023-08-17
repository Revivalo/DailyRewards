package dev.revivalo.dailyrewards.listeners;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.PlayerUtils;
import dev.revivalo.dailyrewards.utils.VersionUtils;
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

            if (user.getAvailableRewards().isEmpty())
                return;

            if (user.hasEnabledAutoClaim()) {
                if (player.hasPermission("dailyreward.autoclaim")) {

                    Bukkit.getScheduler().runTaskLater(DailyRewardsPlugin.get(), () ->
                            DailyRewardsPlugin.getRewardManager().autoClaim(player, availableRewards), 3);
                }
                return;
            }

            if (!user.hasEnabledJoinNotification())
                return;

            if (PlayerUtils.isPlayerInDisabledWorld(player, false))
                return;

            DailyRewardsPlugin.get().runDelayed(() -> {
                PlayerUtils.playSound(player, Config.JOIN_NOTIFICATION_SOUND.asString());
                for (String line : Lang.JOIN_NOTIFICATION.asReplacedList(new HashMap<String, String>() {{
                    put("%rewards%", String.valueOf(availableRewards.size()));
                }})) {
                    BaseComponent[] msg = TextComponent.fromLegacyText(line);

                    for (BaseComponent bc : msg) {
                        if (!VersionUtils.isLegacyVersion()) bc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Lang.UNCLAIMED_REWARDS_NOTIFICATION_HOVER_TEXT.asColoredString())));
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