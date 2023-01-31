package cz.revivalo.dailyrewards.commandmanager.subcommands;

import cz.revivalo.dailyrewards.commandmanager.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RewardDefaultCommand implements SubCommand {
    @Override
    public String getName() {
        return "default";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/reward";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return sender.hasPermission("dailyreward.manage") ? Arrays.asList("reset", "reload", "help", "claim") : Arrays.asList("claim", "help");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

    }
}
