package cz.revivalo.dailyrewards.listeners;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.configuration.data.DataManager;
import cz.revivalo.dailyrewards.configuration.enums.Config;
import cz.revivalo.dailyrewards.configuration.enums.Lang;
import cz.revivalo.dailyrewards.managers.database.MySQLManager;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
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

import java.util.Collection;

public class PlayerJoinListener implements Listener {

	@Getter
	public static final PlayerJoinListener instance = new PlayerJoinListener();

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (DataManager.isUsingMysql()) MySQLManager.createPlayer(player.getUniqueId().toString());
		final Collection<RewardType> availableRewards = DataManager.getAvailableRewards(player);
		final short numberOfAvailableRewards = (short) availableRewards.size();
		if (numberOfAvailableRewards <= 0) return;

		if (Config.AUTO_CLAIM_REWARDS_ON_JOIN.asBoolean()) {
			if (!player.hasPermission("dailyreward.autoclaim")) return;
			Bukkit.getScheduler().runTaskLater(DailyRewards.getPlugin(), () ->
					DailyRewards.getRewardManager().autoClaim(player, availableRewards), 2);
			return;
		}

		if (!Config.ENABLE_JOIN_NOTIFICATION.asBoolean()) return;
		Bukkit.getScheduler().runTaskLater(
				DailyRewards.getPlugin(),
				() -> Lang.JOIN_NOTIFICATION.asColoredList("%rewards%", String.valueOf(numberOfAvailableRewards))
						.stream()
						.map(TextComponent::new)
						.forEach(joinMsg -> {
							joinMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rewards"));
							joinMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder(Lang.JOIN_HOVER_MESSAGE.asPlaceholderReplacedText(player)).create()));

							player.spigot().sendMessage(joinMsg);
						}), Config.JOIN_NOTIFICATION_DELAY.asInt() * 20L);
	}
}
