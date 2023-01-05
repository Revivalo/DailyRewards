package cz.revivalo.dailyrewards.commands;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.RewardType;
import cz.revivalo.dailyrewards.files.Config;
import cz.revivalo.dailyrewards.files.Lang;
import cz.revivalo.dailyrewards.guimanager.GuiManager;
import cz.revivalo.dailyrewards.managers.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class RewardCommand implements CommandExecutor {
    private final RewardManager rewardManager;
    private final GuiManager guiManager;
    public RewardCommand(final DailyRewards plugin) {
        rewardManager = plugin.getRewardManager();
        guiManager = plugin.getGuiManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //final boolean fromPlayer = sender instanceof Player;
        if (!(sender instanceof Player)){
            sender.sendMessage("[DailyRewards] Only in-game command!");
            return true;
        } else {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("reward")){
                switch (args.length){
                    case 0:
                        rewardManager.claim(player, RewardType.DAILY, true, true);
                        break;
                    case 1:
                        switch (args[0]){
                            case "weekly":
                                rewardManager.claim(player, RewardType.WEEKLY, true, true);
                                break;
                            case "monthly":
                                rewardManager.claim(player, RewardType.MONTHLY, true, true);
                                break;
                            case "help":
                                for (final String line : Lang.HELP_MESSAGE.asColoredList()){
                                    player.sendMessage(line);
                                }
                            case "reload":
                                if (!player.hasPermission("dailyreward.manage")){
                                    player.sendMessage(Lang.PERMISSION_MESSAGE.asColoredString());
                                } else {
                                    Config.reload();
                                    Lang.reload();
                                    player.sendMessage(Lang.RELOAD_MESSAGE.asColoredString());
                                }
                                break;
                            default:
                                player.sendMessage(Lang.INVALID_ARGUMENTS_MESSAGE.asColoredString());
                                break;
                        }
                        break;
                    case 2:
                        if (args[0].equalsIgnoreCase("reset")){
                            player.sendMessage(Lang.INCOMPLETE_REWARD_RESET.asColoredString());
                        }
                        break;
                    case 3:
                        switch (args[0]){
                            case "reset":
                                if (!player.hasPermission("dailyreward.manage")){
                                    player.sendMessage(Lang.PERMISSION_MESSAGE.asPlaceholderApiReplacedString(player));
                                } else {
                                    if (rewardManager.reset(Bukkit.getOfflinePlayer(args[1]), RewardType.valueOf(args[2].toUpperCase(Locale.ENGLISH)))) {
                                        player.sendMessage(Lang.REWARD_RESET.asColoredString().replace("%type%", args[2]).replace("%player%", args[1]));
                                    } else {
                                        player.sendMessage(Lang.UNAVAILABLE_PLAYER.asColoredString().replace("%player%", args[1]));
                                    }
                                }
                                break;
                            default:
                                player.sendMessage(Lang.INVALID_ARGUMENTS_MESSAGE.asColoredString());
                                break;
                        }
                        break;
                    default:
                        player.sendMessage(Lang.INVALID_ARGUMENTS_MESSAGE.asColoredString());
                        break;
                }
            } else if (cmd.getName().equalsIgnoreCase("rewards")){
                guiManager.openRewardsMenu(player);
            }
        }
        return true;
    }
}
