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

import java.util.Collections;
import java.util.Locale;

public class RewardCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		/*if (!(sender instanceof Player)) {
			sender.sendMessage("[DailyRewards] Commands are only executable in-game!");
			return true;
		}*/
		//final Player player = (Player) sender;
		final boolean executedFromGame = sender instanceof Player;
		switch (cmd.getName().toLowerCase()) {
			case "reward":
				switch (args.length) {
					case 0:
						if (!executedFromGame){
							sender.sendMessage("[DailyRewards] Command is only executable in-game!");
							return true;
						}
						DailyRewards.getRewardManager().claim((Player) sender, RewardType.DAILY, true, true);
						break;

					case 1:
						switch (args[0]) {
							case "daily":
							case "monthly":
							case "weekly":
								if (!executedFromGame){
									sender.sendMessage("[DailyRewards] Command is only executable in-game!");
									return true;
								}
								DailyRewards.getRewardManager().claim((Player) sender, RewardType.findByName(args[0]), true, true);
								break;

							case "help":
								if (!sender.hasPermission("dailyreward.help")){
									sender.sendMessage(Lang.PERMISSION_MESSAGE.asColoredString());
									break;
								}
								Lang.HELP_MESSAGE.asColoredList(Collections.emptyMap()).forEach(sender::sendMessage);
								break;

							case "reload":
								if (!sender.hasPermission("dailyreward.manage")) {
									sender.sendMessage(Lang.PERMISSION_MESSAGE.asColoredString());
									break;
								}
								Config.reload();
								Lang.reload();
								sender.sendMessage(Lang.RELOAD_MESSAGE.asColoredString());
								break;

							default:
								sender.sendMessage(Lang.INVALID_ARGUMENTS_MESSAGE.asColoredString());
								break;
						}
						break;

					case 2:
						if (!"reset".equalsIgnoreCase(args[0])) break;
						sender.sendMessage(Lang.INCOMPLETE_REWARD_RESET.asColoredString());
						break;

					case 3:
						if ("reset".equals(args[0])) {
							if (!sender.hasPermission("dailyreward.manage")) {
								sender.sendMessage(Lang.PERMISSION_MESSAGE.asColoredString());
								break;
							}

							try {
								sender.sendMessage(DailyRewards.getRewardManager()
										.resetPlayer(Bukkit.getOfflinePlayer(args[1]), RewardType.valueOf(args[2].toUpperCase(Locale.ENGLISH))) ?
										Lang.REWARD_RESET.asColoredString().replace("%type%", args[2]).replace("%player%", args[1]) :
										Lang.UNAVAILABLE_PLAYER.asColoredString().replace("%player%", args[1]));
								break;
							} catch (IllegalArgumentException ex){
								sender.sendMessage(Lang.INCOMPLETE_REWARD_RESET.asColoredString());
								break;
							}
						}
						sender.sendMessage(Lang.INVALID_ARGUMENTS_MESSAGE.asColoredString());
						break;

					default:
						sender.sendMessage(Lang.INVALID_ARGUMENTS_MESSAGE.asColoredString());
						break;
				}
				break;

			case "rewards":
				if (!executedFromGame){
					sender.sendMessage("[DailyRewards] Command is only executable in-game!");
					return true;
				}
				DailyRewards.getMenuManager().openRewardsMenu((Player) sender);
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
