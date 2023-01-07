package cz.revivalo.dailyrewards.commands;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.configuration.enums.Config;
import cz.revivalo.dailyrewards.configuration.enums.Lang;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class RewardCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("[DailyRewards] Commands are only executable in-game!");
			return true;
		}
		final Player player = (Player) sender;

		switch (cmd.getName().toLowerCase()) {
			case "reward":
				switch (args.length) {
					case 0:
						DailyRewards.getRewardManager().claim(player, RewardType.DAILY, true, true);
						break;

					case 1:
						switch (args[0]) {
							case "daily":
							case "monthly":
							case "weekly":
								DailyRewards.getRewardManager().claim(player, RewardType.findByName(args[0]), true, true);
								break;

							case "help":
								Lang.HELP_MESSAGE.asColoredList().forEach(player::sendMessage);
								break;

							case "reload":
								if (!player.hasPermission("dailyreward.manage")) {
									player.sendMessage(Lang.PERMISSION_MESSAGE.asColoredString());
									break;
								}
								Config.reload();
								Lang.reload();
								player.sendMessage(Lang.RELOAD_MESSAGE.asColoredString());
								break;

							default:
								player.sendMessage(Lang.INVALID_ARGUMENTS_MESSAGE.asColoredString());
								break;
						}
						break;

					case 2:
						if (!"reset".equalsIgnoreCase(args[0])) break;
						player.sendMessage(Lang.INCOMPLETE_REWARD_RESET.asColoredString());
						break;

					case 3:
						if ("reset".equals(args[0])) {
							if (!player.hasPermission("dailyreward.manage")) {
								player.sendMessage(Lang.PERMISSION_MESSAGE.asPlaceholderReplacedText(player));
								break;
							}

							player.sendMessage(DailyRewards.getRewardManager()
									.resetPlayer(Bukkit.getOfflinePlayer(args[1]), RewardType.valueOf(args[2].toUpperCase(Locale.ENGLISH))) ?
									Lang.REWARD_RESET.asColoredString().replace("%type%", args[2]).replace("%player%", args[1]) :
									Lang.UNAVAILABLE_PLAYER.asColoredString().replace("%player%", args[1]));
							break;
						}
						player.sendMessage(Lang.INVALID_ARGUMENTS_MESSAGE.asColoredString());
						break;

					default:
						player.sendMessage(Lang.INVALID_ARGUMENTS_MESSAGE.asColoredString());
						break;
				}
				break;

			case "rewards":
				DailyRewards.getMenuManager().openRewardsMenu(player);
				break;
		}
		return true;
	}

	/*@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (command.getName().equalsIgnoreCase("reward")){
			if (args.length == 0){
				return
			}
		}
		return null;
	}*/
}
