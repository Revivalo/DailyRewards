package cz.revivalo.dailyrewards.commandmanager.commands;

import cz.revivalo.dailyrewards.commandmanager.MainCommand;
import cz.revivalo.dailyrewards.commandmanager.argumentmatchers.StartingWithStringArgumentMatcher;
import cz.revivalo.dailyrewards.commandmanager.subcommands.RewardsDefaultCommand;
import cz.revivalo.dailyrewards.configuration.enums.Lang;

public class RewardsMainCommand extends MainCommand {
    public RewardsMainCommand() {
        super(Lang.PERMISSION_MESSAGE.asColoredString(), new StartingWithStringArgumentMatcher());
    }

    @Override
    protected void registerSubCommands() {
        subCommands.add(new RewardsDefaultCommand());
    }
}
