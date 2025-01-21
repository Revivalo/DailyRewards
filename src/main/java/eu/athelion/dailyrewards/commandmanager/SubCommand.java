package eu.athelion.dailyrewards.commandmanager;

import eu.athelion.dailyrewards.configuration.file.Lang;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    String getName();

    Lang getDescription();

    String getSyntax();

    String getPermission();

    List<String> getTabCompletion(CommandSender sender, int index, String[] args);

    void perform(CommandSender sender, String[] args);
}
