package dev.revivalo.dailyrewards.listeners;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.PlayerUtils;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

    @Getter
    public static final PlayerJoinQuitListener instance = new PlayerJoinQuitListener();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        DataManager.loadPlayerDataAsync(player, cooldowns -> {

            User user = UserHandler.addUser(
                    new User(
                            player,
                            cooldowns
                    )
            );

            final Set<RewardType> availableRewards = user.getAvailableRewards();

            if (user.getAvailableRewards().size() == 0)
                return;

            if (user.isEnabledAutoClaim()) {
                if (!player.hasPermission("dailyreward.autoclaim"))
                    return;

                Bukkit.getScheduler().runTaskLater(DailyRewardsPlugin.get(), () ->
                        DailyRewardsPlugin.getRewardManager().autoClaim(player, availableRewards), 3);
                return;
            }

            if (!user.isEnabledJoinNotification())
                return;

            if (PlayerUtils.isPlayerInDisabledWorld(player, false))
                return;

            Bukkit.getScheduler().runTaskLater(
                    DailyRewardsPlugin.get(),
                    () -> Lang.JOIN_NOTIFICATION.asReplacedList(new HashMap<String, String>() {{
                                put("%rewards%", String.valueOf(availableRewards.size()));
                            }})
                            .stream()
                            .map(TextComponent::new)
                            .forEach(joinMsg -> {
                                joinMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Config.JOIN_NOTIFICATION_COMMAND.asString()));
                                joinMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder(Lang.JOIN_HOVER_MESSAGE.asPlaceholderReplacedText(player)).create())
                                );

                                player.spigot().sendMessage(joinMsg);
                            }), Config.JOIN_NOTIFICATION_DELAY.asInt() * 20L);

        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onQuit(final PlayerQuitEvent event) {
        UserHandler.removeUser(event.getPlayer().getUniqueId());
    }
}
