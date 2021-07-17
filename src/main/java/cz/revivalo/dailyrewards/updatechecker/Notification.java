package cz.revivalo.dailyrewards.updatechecker;

import cz.revivalo.dailyrewards.DailyRewards;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Notification implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if (p.isOp()){
            if (!DailyRewards.newestVersion){
                p.sendMessage("§eThere is a new version of PlayerWarps plugin.");
                p.sendMessage("§eRemember, that for outdated versions isn't provided support.");
                p.sendMessage("§e§nhttps://bit.ly/revivalo-dailyrewards");
            }
        }
    }
}
