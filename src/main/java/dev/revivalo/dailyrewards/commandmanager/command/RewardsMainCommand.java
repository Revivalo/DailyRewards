package dev.revivalo.dailyrewards.commandmanager.command;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.commandmanager.MainCommand;
import dev.revivalo.dailyrewards.commandmanager.argumentmatcher.StartingWithStringArgumentMatcher;
import dev.revivalo.dailyrewards.configuration.file.Lang;
import dev.revivalo.dailyrewards.util.PermissionUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RewardsMainCommand extends MainCommand {
    public RewardsMainCommand() {
        super(Lang.INSUFFICIENT_PERMISSION, new StartingWithStringArgumentMatcher());
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

        if (!PermissionUtil.hasPermission(player, PermissionUtil.Permission.OPENS_MAIN_REWARD_MENU)) {
            sender.sendMessage(Lang.INSUFFICIENT_PERMISSION.asColoredString(player)
                    .replace("%permission%", PermissionUtil.Permission.OPENS_MAIN_REWARD_MENU.get()));
            return;
        }

        DailyRewardsPlugin.getMenuManager().openRewardsMenu((Player) sender);
    }
}
