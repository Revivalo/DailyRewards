package cz.revivalo.dailyrewards.commandmanager.subcommands;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.commandmanager.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RewardsDefaultCommand implements SubCommand {
    @Override
    public String getName() {
        return "default";
    }

    @Override
    public String getDescription() {
        return "Opens a menu with rewards";
    }

    @Override
    public String getSyntax() {
        return "/rewards";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return sender.hasPermission("dailyrewards.manage") ? Arrays.asList("reload", "reset", "claim", "help") : Collections.singletonList("claim");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        DailyRewards.getMenuManager().openRewardsMenu((Player) sender);
    }
}
