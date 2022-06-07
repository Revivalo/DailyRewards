package cz.revivalo.dailyrewards.rewardmanager;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.lang.Lang;
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
        if (!Lang.AUTOMATICALLYACTIVATE.getBoolean()) cooldowns.set(player);
        if (Lang.ENABLEJOINNOTIFICATION.getBoolean()) {
            int available = 0;
            for (int i = 0; i <= 3; i++) {
                if (i == 0) {
                    if (Long.parseLong(cooldowns.getCooldown(player, "daily", false)) < 0 && (player.hasPermission("dailyreward.daily") || player.hasPermission("dailyreward.daily.premium"))) ++available;
                } else if (i == 1) {
                    if (Long.parseLong(cooldowns.getCooldown(player, "weekly", false)) < 0 && (player.hasPermission("dailyreward.weekly") || player.hasPermission("dailyreward.weekly.premium"))) ++available;
                } else if (i == 2) {
                    if (Long.parseLong(cooldowns.getCooldown(player, "monthly", false)) < 0 && (player.hasPermission("dailyreward.monthly") || player.hasPermission("dailyreward.monthly.premium"))) ++available;
                }
            }
            if (available != 0) {
                int finalAvailable = available;
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        for (String line : Lang.JOINNOTIFICATION.contentLore(player)){
                            TextComponent joinMsg = new TextComponent(line.replace("%rewards%", String.valueOf(finalAvailable)));
                            joinMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rewards"));
                            joinMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Lang.JOINHOVERMESSAGE.content(player)).create()));
                            player.spigot().sendMessage(joinMsg);
                        }
                    }
                }.runTaskLater(plugin, Lang.JOINNOTIFICATIONDELAY.getInt() * 20L);
            }
        }
    }
}
