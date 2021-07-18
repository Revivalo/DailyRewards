package cz.revivalo.dailyrewards.updatechecker;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.lang.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Notification implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if (p.isOp()){
            if (Boolean.parseBoolean(Lang.UPDATECHECKER.content())) {
                if (!DailyRewards.newestVersion) {
                    p.sendMessage("§e[§6§lDailyRewards§e] There is a new version of plugin. Download:");
                    p.sendMessage("§e§nhttps://bit.ly/revivalo-dailyrewards");
                }
            }
        }
    }
}
