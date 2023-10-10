package dev.revivalo.dailyrewards.commandmanager.subcommands;

import dev.revivalo.dailyrewards.commandmanager.SubCommand;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class HelpCommand implements SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Sends a list with a commands and instructions";
    }

    @Override
    public String getSyntax() {
        return "/reward help";
    }

    @Override
    public String getPermission() {
        return PermissionUtils.Permission.HELP.get();
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Lang.HELP_MESSAGE.asReplacedList(Collections.emptyMap()).forEach(sender::sendMessage);
    }
}
