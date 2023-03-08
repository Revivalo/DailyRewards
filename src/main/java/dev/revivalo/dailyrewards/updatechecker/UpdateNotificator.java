package dev.revivalo.dailyrewards.updatechecker;

import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.utils.VersionUtils;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateNotificator implements Listener {

	@Getter
	public static final UpdateNotificator instance = new UpdateNotificator();

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void playerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		if (!player.isOp()) return;
		if (!Config.UPDATE_CHECKER.asBoolean()) return;
		if (VersionUtils.isLatestVersion()) return;

		player.sendMessage("§e[§6§lDailyRewards§e] There is a newer version of this plugin. Download: " +
				"\n§e§nhttps://bit.ly/revivalo-dailyrewards");
	}
}
