package dev.revivalo.dailyrewards.commandmanager.commands;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.commandmanager.MainCommand;
import dev.revivalo.dailyrewards.commandmanager.argumentmatchers.StartingWithStringArgumentMatcher;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RewardsMainCommand extends MainCommand {
    public RewardsMainCommand() {
        super(Lang.PERMISSION_MESSAGE.asColoredString(), new StartingWithStringArgumentMatcher());
    }

    @Override
    protected void registerSubCommands() {

    }

    @Override
    protected void perform(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[DailyRewards] This command is only executable in-game!");
            return;
        }

        if (sender.hasPermission("dailyreward.use"))
            DailyRewardsPlugin.getMenuManager().openRewardsMenu((Player) sender);
        else
            sender.sendMessage(Lang.PERMISSION_MESSAGE.asColoredString());
    }
}
