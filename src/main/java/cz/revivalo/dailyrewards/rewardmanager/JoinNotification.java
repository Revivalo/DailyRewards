package cz.revivalo.dailyrewards.rewardmanager;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.lang.Lang;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinNotification implements Listener {
    private final DailyRewards plugin;
    private final Cooldowns cooldowns;
    public JoinNotification(DailyRewards plugin) {
        this.plugin = plugin;
        cooldowns = plugin.getCooldowns();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if (!Boolean.parseBoolean(Lang.AUTOMATICALLYACTIVATE.content(p))){
            cooldowns.set(e.getPlayer());
        }
        if (Boolean.parseBoolean(Lang.ENABLEJOINNOTIFICATION.content(p))) {
            int available = 0;
            for (int i = 0; i <= 3; i++) {
                if (i == 0) {
                    if (Long.parseLong(cooldowns.getCooldown(p, "daily", false)) < 0 && (p.hasPermission("dailyreward.daily") || p.hasPermission("dailyreward.daily.premium"))) ++available;
                } else if (i == 1) {
                    if (Long.parseLong(cooldowns.getCooldown(p, "weekly", false)) < 0 && (p.hasPermission("dailyreward.weekly") || p.hasPermission("dailyreward.weekly.premium"))) ++available;
                } else if (i == 2) {
                    if (Long.parseLong(cooldowns.getCooldown(p, "monthly", false)) < 0 && (p.hasPermission("dailyreward.monthly") || p.hasPermission("dailyreward.monthly.premium"))) ++available;
                }
            }
            if (available != 0) {
                int finalAvailable = available;
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        for (String line : Lang.JOINNOTIFICATION.contentLore(p)){
                            TextComponent joinMsg = new TextComponent(line.replace("%rewards%", String.valueOf(finalAvailable)));
                            joinMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rewards"));
                            joinMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Lang.JOINHOVERMESSAGE.content(p)).create()));
                            p.spigot().sendMessage(joinMsg);
                        }
                    }
                }.runTaskLater(plugin, Integer.parseInt(Lang.JOINNOTIFICATIONDELAY.content(p)) * 20L);
            }
        }
    }
}
