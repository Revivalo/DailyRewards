package dev.revivalo.dailyrewards.commandmanager.command;

import dev.revivalo.dailyrewards.commandmanager.MainCommand;
import dev.revivalo.dailyrewards.commandmanager.argumentmatcher.StartingWithStringArgumentMatcher;
import dev.revivalo.dailyrewards.commandmanager.subcommand.*;
import dev.revivalo.dailyrewards.configuration.file.Lang;
import dev.revivalo.dailyrewards.util.TextUtil;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class RewardMainCommand extends MainCommand {
    public RewardMainCommand() {
        super(Lang.INSUFFICIENT_PERMISSION, new StartingWithStringArgumentMatcher());
    }

    @Override
    protected void registerSubCommands() {
        subCommands.add(new ClaimCommand());
        subCommands.add(new ImportCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new ResetCommand());
        subCommands.add(new SettingsCommand());
        subCommands.add(new HelpCommand());
        subCommands.add(new ToggleCommand());
    }

    @Override
    protected void perform(CommandSender sender) {
        TextUtil.sendListToPlayer(sender, Lang.HELP_MESSAGE.asReplacedList(Collections.emptyMap()));
    }
}
