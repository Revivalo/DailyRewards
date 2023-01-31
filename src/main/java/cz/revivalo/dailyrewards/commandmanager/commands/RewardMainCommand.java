package cz.revivalo.dailyrewards.commandmanager.commands;

import cz.revivalo.dailyrewards.commandmanager.MainCommand;
import cz.revivalo.dailyrewards.commandmanager.argumentmatchers.StartingWithStringArgumentMatcher;
import cz.revivalo.dailyrewards.commandmanager.subcommands.*;
import cz.revivalo.dailyrewards.configuration.enums.Lang;

public class RewardMainCommand extends MainCommand {
    public RewardMainCommand() {
        super(Lang.PERMISSION_MESSAGE.asColoredString(), new StartingWithStringArgumentMatcher());
    }

    @Override
    protected void registerSubCommands() {
        subCommands.add(new RewardDefaultCommand());
        subCommands.add(new ClaimCommand());
        subCommands.add(new HelpCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new ResetCommand());
    }
}
