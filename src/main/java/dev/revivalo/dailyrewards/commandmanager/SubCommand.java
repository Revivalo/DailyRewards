package dev.revivalo.dailyrewards.commandmanager;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    String getName();

    String getDescription();

    String getSyntax();

    String getPermission();

    List<String> getTabCompletion(CommandSender sender, int index, String[] args);

    void perform(CommandSender sender, String[] args);
}
