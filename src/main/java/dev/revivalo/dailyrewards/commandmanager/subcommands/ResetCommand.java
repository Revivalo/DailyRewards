package dev.revivalo.dailyrewards.commandmanager.subcommands;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.commandmanager.SubCommand;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.reward.Reward;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.List;
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
            case 0: return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
            case 1: return DailyRewardsPlugin.getRewardManager().getRewards().stream().map(Reward::getRewardName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length != 2){
            sender.sendMessage(Lang.VALID_COMMAND_USAGE.asColoredString().replace("%usage%", getSyntax()));
            return;
        }
        sender.sendMessage(DailyRewardsPlugin.getRewardManager().resetPlayer(Bukkit.getOfflinePlayer(args[0]), args[1]));
    }
}
