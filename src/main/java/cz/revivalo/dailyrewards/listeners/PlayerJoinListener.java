package cz.revivalo.dailyrewards.listeners;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.RewardType;
import cz.revivalo.dailyrewards.files.Config;
import cz.revivalo.dailyrewards.files.Lang;
import cz.revivalo.dailyrewards.files.DataManager;
import cz.revivalo.dailyrewards.managers.MySQLManager;
import cz.revivalo.dailyrewards.managers.RewardManager;
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

import java.util.Collection;

public class PlayerJoinListener implements Listener {
    private final DailyRewards plugin;
    private final RewardManager rewardManager;
    public PlayerJoinListener(final DailyRewards plugin) {
        this.plugin = plugin;
        this.rewardManager = plugin.getRewardManager();
    }

    @EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(final PlayerJoinEvent event){
        final Player player = event.getPlayer();
        //if (!Config.AUTOMATICALLY_ACTIVATE.getBoolean()) cooldowns.set(player);
        if (DataManager.isUsingMysql()) MySQLManager.createPlayer(player.getUniqueId().toString());
        final Collection<RewardType> availableRewards = DataManager.getAvailableRewards(player);
        if (availableRewards.size() > 0 && Config.AUTO_CLAIM_REWARDS_ON_JOIN.asBoolean() && player.hasPermission("dailyreward.autoclaim")) {
            new BukkitRunnable(){
                @Override
                public void run() {
                    rewardManager.autoClaim(player, availableRewards);
                }
            }.runTaskLater(plugin, 2);
            return;
        }
        if (Config.ENABLE_JOIN_NOTIFICATION.asBoolean()) {
            short numberOfAvailableRewards = (short) availableRewards.size();
            if (numberOfAvailableRewards == 0) {
                return;
            }
            new BukkitRunnable(){
                @Override
                public void run() {
                    for (final String line : Lang.JOIN_NOTIFICATION.asColoredList("%rewards%", String.valueOf(numberOfAvailableRewards))){
                        TextComponent joinMsg = new TextComponent(line);
                        joinMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rewards"));
                        joinMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Lang.JOIN_HOVER_MESSAGE.asPlaceholderApiReplacedString(player)).create()));
                        player.spigot().sendMessage(joinMsg);
                    }
                }
            }.runTaskLater(plugin, Config.JOIN_NOTIFICATION_DELAY.asInt() * 20L);
        }
    }
}
