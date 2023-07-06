package dev.revivalo.dailyrewards.updatechecker;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.utils.VersionUtils;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateNotificator implements Listener {

	@Getter
	public static final UpdateNotificator instance = new UpdateNotificator();

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void playerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		if (!player.isOp()) return;
		if (!Config.UPDATE_CHECKER.asBoolean()) return;
		if (VersionUtils.isLatestVersion()) return;

		TextComponent download = new TextComponent("§6§lDownload");
		download.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/%E2%9A%A1-daily-weekly-monthly-rewards-mysql-oraxen-itemsadder-support-1-8-1-19-4.81780/"));

		TextComponent changelog = new TextComponent("§6§lChangelog");
		changelog.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/%E2%9A%A1-daily-weekly-monthly-rewards-mysql-oraxen-itemsadder-support-1-8-1-19-4.81780/updates"));

		TextComponent upgrade = new TextComponent("§6§lUpgrade");
		upgrade.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/%E2%9C%A8-ultimaterewards-create-a-whole-network-reward-system-with-ease-1-8-1-19-4.108055/"));

		TextComponent donate = new TextComponent("§6§lDonate");
		donate.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.paypal.me/revivalo"));


		new BukkitRunnable() {
			@Override
			public void run() {
				player.sendMessage(" ");
				player.sendMessage("§7There is a new version of §6DailyRewards§7 available.");
				player.spigot().sendMessage(download, new TextComponent(" §8| "), upgrade, new TextComponent(" §8| "), changelog, new TextComponent(" §8| "), donate);
				player.sendMessage("§8Latest version: §a" + DailyRewardsPlugin.getLatestVersion() + " §8| Your version: §c" + DailyRewardsPlugin.get().getDescription().getVersion());
				player.sendMessage(" ");
			}
		}.runTaskLater(DailyRewardsPlugin.get(), 35);
	}
}
