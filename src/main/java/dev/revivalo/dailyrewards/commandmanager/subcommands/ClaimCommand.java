package dev.revivalo.dailyrewards.commandmanager.subcommands;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.commandmanager.SubCommand;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ClaimCommand implements SubCommand {
    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getDescription() {
        return "Claims a stated reward";
    }

    @Override
    public String getSyntax() {
        return "/reward claim <daily|weekly|monthly>";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        List<String> commands = Arrays.stream(RewardType.values()).map(reward -> reward.toString()).collect(Collectors.toList());
        commands.remove("all");
        return commands;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        RewardType rewardType;
        try {
            rewardType = RewardType.valueOf(args[0].toUpperCase(Locale.ENGLISH));
        } catch (Exception exception){
            sender.sendMessage(Lang.VALID_COMMAND_USAGE.asColoredString().replace("%usage%", getSyntax()));
            return;
        }
        if (!(sender instanceof Player)){
            sender.sendMessage("[DailyRewards] Command is only executable in-game!");
            return;
        }
        DailyRewardsPlugin.getRewardManager().claim((Player) sender, rewardType, true, true);
    }
}
