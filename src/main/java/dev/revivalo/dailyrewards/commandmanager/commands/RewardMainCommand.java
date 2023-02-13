package dev.revivalo.dailyrewards.commandmanager.commands;

import dev.revivalo.dailyrewards.commandmanager.MainCommand;
import dev.revivalo.dailyrewards.commandmanager.argumentmatchers.StartingWithStringArgumentMatcher;
import dev.revivalo.dailyrewards.commandmanager.subcommands.ClaimCommand;
import dev.revivalo.dailyrewards.commandmanager.subcommands.HelpCommand;
import dev.revivalo.dailyrewards.commandmanager.subcommands.ReloadCommand;
import dev.revivalo.dailyrewards.commandmanager.subcommands.ResetCommand;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.utils.TextUtils;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class RewardMainCommand extends MainCommand {
    public RewardMainCommand() {
        super(Lang.PERMISSION_MESSAGE.asColoredString(), new StartingWithStringArgumentMatcher());
    }

    @Override
    protected void registerSubCommands() {
        subCommands.add(new ClaimCommand());
        subCommands.add(new HelpCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new ResetCommand());
    }

    @Override
    protected void perform(CommandSender sender) {
        TextUtils.sendListToPlayer(sender, Lang.HELP_MESSAGE.asReplacedList(Collections.emptyMap()));
    }
}
