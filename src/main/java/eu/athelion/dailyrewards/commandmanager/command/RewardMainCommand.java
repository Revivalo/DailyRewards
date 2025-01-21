package eu.athelion.dailyrewards.commandmanager.command;

import eu.athelion.dailyrewards.commandmanager.MainCommand;
import eu.athelion.dailyrewards.commandmanager.argumentmatcher.StartingWithStringArgumentMatcher;
import eu.athelion.dailyrewards.commandmanager.subcommand.*;
import eu.athelion.dailyrewards.configuration.file.Lang;
import org.bukkit.command.CommandSender;

public class RewardMainCommand extends MainCommand {
    public RewardMainCommand() {
        super(Lang.INSUFFICIENT_PERMISSION, new StartingWithStringArgumentMatcher());
    }

    @Override
    protected void registerSubCommands() {
        subCommands.add(new ClaimCommand());
        subCommands.add(new ResetCommand());
        subCommands.add(new SettingsCommand());
        subCommands.add(new ToggleCommand());
        subCommands.add(new ImportCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new HelpCommand(this));
    }

    @Override
    protected void perform(CommandSender sender) {
        //TextUtil.sendListToPlayer(sender, Lang.HELP_MESSAGE.asReplacedList(Collections.emptyMap()));
    }
}
