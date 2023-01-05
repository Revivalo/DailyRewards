package cz.revivalo.dailyrewards.updatechecker;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.files.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateNotificator implements Listener {

    @EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(final PlayerJoinEvent event){
        final Player player = event.getPlayer();
        if (player.isOp()){
            if (Config.UPDATE_CHECKER.asBoolean()) {
                if (!DailyRewards.newestVersion) {
                    player.sendMessage("§e[§6§lDailyRewards§e] There is a new version of plugin. Download:");
                    player.sendMessage("§e§nhttps://bit.ly/revivalo-dailyrewards");
                }
            }
        }
    }
}
