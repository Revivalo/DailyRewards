package dev.revivalo.dailyrewards.commandmanager.subcommand;

import dev.revivalo.dailyrewards.commandmanager.SubCommand;
import dev.revivalo.dailyrewards.configuration.file.Lang;
import dev.revivalo.dailyrewards.util.PermissionUtil;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class HelpCommand implements SubCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public Lang getDescription() {
        return Lang.HELP_COMMAND_DESCRIPTION;
    }

    @Override
    public String getSyntax() {
        return "/reward help";
    }

    @Override
    public String getPermission() {
        return PermissionUtil.Permission.HELP.get();
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