package cz.revivalo.dailyrewards.rewardmanager;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.files.Lang;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinNotification implements Listener {
    private final DailyRewards plugin;
    private final Cooldowns cooldowns;
    public JoinNotification(final DailyRewards plugin) {
        this.plugin = plugin;
        cooldowns = plugin.getCooldowns();
    }

    @EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(final PlayerJoinEvent event){
        final Player player = event.getPlayer();
        if (!Lang.AUTOMATICALLY_ACTIVATE.getBoolean()) cooldowns.set(player);
        if (Lang.ENABLE_JOIN_NOTIFICATION.getBoolean()) {
            int available = 0;
            for (int i = 0; i <= 3; i++) {
                if (i == 0) {
                    if (cooldowns.isRewardAvailable(player, "daily") && (player.hasPermission("dailyreward.daily") || player.hasPermission("dailyreward.daily.premium"))) ++available;
                } else if (i == 1) {
                    if (cooldowns.isRewardAvailable(player, "weekly") && (player.hasPermission("dailyreward.weekly") || player.hasPermission("dailyreward.weekly.premium"))) ++available;
                } else if (i == 2) {
                    if (cooldowns.isRewardAvailable(player, "monthly") && (player.hasPermission("dailyreward.monthly") || player.hasPermission("dailyreward.monthly.premium"))) ++available;
                }
            }
            if (available == 0) {
                return;
            }
            int finalAvailable = available;
            new BukkitRunnable(){
                @Override
                public void run() {
                    for (final String line : Lang.JOIN_NOTIFICATION.getColoredList(player, "%rewards%", String.valueOf(finalAvailable))){
                        TextComponent joinMsg = new TextComponent(line);
                        joinMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rewards"));
                        joinMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Lang.JOIN_HOVER_MESSAGE.content(player)).create()));
                        player.spigot().sendMessage(joinMsg);
                    }
                }
            }.runTaskLater(plugin, Lang.JOIN_NOTIFICATION_DELAY.getInt() * 20L);
        }
    }
}
