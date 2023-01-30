package cz.revivalo.dailyrewards.commandmanager.subcommands;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.commandmanager.SubCommand;
import cz.revivalo.dailyrewards.configuration.enums.Lang;
import cz.revivalo.dailyrewards.managers.reward.RewardManager;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ResetCommand implements SubCommand {
    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "Resets a specified reward";
    }

    @Override
    public String getSyntax() {
        return "/reward reset <player> <daily|weekly|monthly|all>";
    }

    @Override
    public String getPermission() {
        return "dailyreward.manage";
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        switch (index){
            case 0:
                return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
            case 1:
                return Arrays.stream(RewardType.values()).map(reward -> reward.toString()).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 1){
            sender.sendMessage(Lang.INVALID_ARGUMENTS_MESSAGE.asColoredString());
            return;
        }
        try {
            sender.sendMessage(DailyRewards.getRewardManager()
                    .resetPlayer(Bukkit.getOfflinePlayer(args[0]), RewardType.valueOf(args[1].toUpperCase(Locale.ENGLISH))) ?
                    Lang.REWARD_RESET.asColoredString().replace("%type%", args[1]).replace("%player%", args[0]) :
                    Lang.UNAVAILABLE_PLAYER.asColoredString().replace("%player%", args[0]));
        } catch (IllegalArgumentException ex){
            sender.sendMessage(Lang.INCOMPLETE_REWARD_RESET.asColoredString());
        }
    }
}
