package dev.revivalo.dailyrewards.managers.reward;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.cooldown.Cooldown;
import dev.revivalo.dailyrewards.managers.cooldown.CooldownManager;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class RewardManager {

	public void autoClaim(final Player player, Collection<RewardType> rewardTypes) {
		if (Config.CHECK_FOR_FULL_INVENTORY.asBoolean() && player.getInventory().firstEmpty() == -1) {
			player.sendMessage(Lang.FULL_INVENTORY_MESSAGE.asColoredString());
			return;
		}
		final String formattedRewards = rewardTypes.stream()
				.map(this::getRewardsPlaceholder)
				.collect(Collectors.joining(", "));

		rewardTypes.forEach(rewardType -> this.claim(player, rewardType, false, false));
		TextUtils.sendListToPlayer(player, Lang.AUTO_CLAIMED_NOTIFICATION
				.asReplacedList(new HashMap<String, String>() {{put("%rewards%", String.format(formattedRewards));}}));
	}

	@SuppressWarnings("deprecation")
	public void claim(final Player player, RewardType type, boolean fromCommand, boolean announce) {
		if (!type.isEnabled()){
			player.sendMessage(Lang.DISABLED_REWARD.asColoredString());
			return;
		}
		if (!player.hasPermission("dailyreward." + type)) {
			if (!fromCommand) return;
			player.sendMessage(Lang.PERMISSION_MESSAGE.asColoredString());
			return;
		}
		if (Config.CHECK_FOR_FULL_INVENTORY.asBoolean() && player.getInventory().firstEmpty() == -1) {
			player.sendMessage(Lang.FULL_INVENTORY_MESSAGE.asColoredString());
			return;
		}

		final Cooldown cooldown = CooldownManager.getCooldown(player, type);
		if (cooldown.isClaimable()) {
			final String typeName = type.toString().toUpperCase();
			final Collection<String> rewardCommands = Config.valueOf(String.format("%s%s_REWARDS", typeName, DailyRewardsPlugin.isPremium(player, type)))
					.asReplacedList(new HashMap<String, String>(){{put("%player%", player.getName());}});

			if (rewardCommands.size() == 0) {
				player.sendMessage(Lang.REWARDS_IS_NOT_SET.asColoredString());
			} else {
				rewardCommands.forEach(command -> Bukkit.dispatchCommand(DailyRewardsPlugin.getConsole(), command));
			}

			Optional<User> userOptional = UserHandler.getUser(player.getUniqueId());
			userOptional.ifPresent(user -> user.setAvailableRewards((short) (user.getAvailableRewards() - 1)));

			CooldownManager.setCooldown(player, type);
			if (announce) {
				player.playSound(player.getLocation(), Sound.valueOf(Config.valueOf(
						String.format("%s_SOUND", typeName)).asUppercase()), 1F, 1F);

				player.sendTitle(
						Lang.valueOf(String.format("%s_TITLE", typeName)).asColoredString(),
						Lang.valueOf(String.format("%s_SUBTITLE", typeName)).asColoredString());

				if (Config.ANNOUNCE_ENABLED.asBoolean()) {
					Bukkit.broadcastMessage(Lang.valueOf(String.format("%s%s_COLLECTED", typeName, DailyRewardsPlugin.isPremium(player, type)))
							.asPlaceholderReplacedText(player)
							.replace("%player%", player.getName()));
				}
			}
			if (!fromCommand) player.closeInventory();

		} else {
			if (fromCommand) {
				player.sendMessage(Lang.COOLDOWN_MESSAGE.asColoredString()
						.replace("%type%", getRewardsPlaceholder(type))
						.replace("%time%", cooldown.getFormat(type.getCooldownFormat())));
				return;
			}
			player.playSound(player.getLocation(),
					Sound.valueOf(Config.UNAVAILABLE_REWARD_SOUND
							.asString()
							.toUpperCase(Locale.ENGLISH)),
					1F, 1F);
		}
	}

	public String resetPlayer(final OfflinePlayer player, String type) {
		final boolean isPlayerOnline = player.isOnline();
		if (!isPlayerOnline && !player.hasPlayedBefore())
			return Lang.UNAVAILABLE_PLAYER.asColoredString().replace("%player%", player.getName());
		if (type.equalsIgnoreCase("all")) {
			DataManager.setValues(player.getUniqueId(),
					new HashMap<String, Object>(){{
						put(RewardType.DAILY.toString(), 0L);
						put(RewardType.WEEKLY.toString(), 0L);
						put(RewardType.MONTHLY.toString(), 0L);
					}}
			);
		} else {
			try {
				DataManager.setValues(player.getUniqueId(), new HashMap<String, Object>() {{
					put(type, 0L);
				}});
			} catch (IllegalArgumentException ex) {
				return Lang.INCOMPLETE_REWARD_RESET.asColoredString();
			}
		}

		Bukkit.getLogger().info(DataManager.getAvailableRewards(player.getPlayer()).size() + "");
		if (isPlayerOnline) UserHandler.getUser(player.getUniqueId()).get().setAvailableRewards((short) DataManager.getAvailableRewards(player.getPlayer()).size());

		return Lang.REWARD_RESET.asColoredString().replace("%type%", type).replace("%player%", player.getName());
	}

	private String getRewardsPlaceholder(final RewardType reward) {
		switch (reward) {
			case DAILY:
				return Config.DAILY_PLACEHOLDER.asString();
			case WEEKLY:
				return Config.WEEKLY_PLACEHOLDER.asString();
		}
		return Config.MONTHLY_PLACEHOLDER.asString();
	}
}
