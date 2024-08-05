package dev.revivalo.dailyrewards.commandmanager.commands;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.commandmanager.MainCommand;
import dev.revivalo.dailyrewards.commandmanager.argumentmatchers.StartingWithStringArgumentMatcher;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RewardsMainCommand extends MainCommand {
    public RewardsMainCommand() {
        super(Lang.PERMISSION_MSG.asColoredString(), new StartingWithStringArgumentMatcher());
    }

    @Override
    protected void registerSubCommands() {
        // Command hasn't any subcommands
    }

    @Override
    protected void perform(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[DailyRewards] This command is only executable in-game!");
            return;
        }

        final Player player = (Player) sender;

        if (!PermissionUtils.hasPermission(player, PermissionUtils.Permission.OPENS_MAIN_REWARD_MENU)) {
            sender.sendMessage(Lang.PERMISSION_MSG.asColoredString(player));
            return;
        }

        DailyRewardsPlugin.getMenuManager().openRewardsMenu((Player) sender);
    }
}
