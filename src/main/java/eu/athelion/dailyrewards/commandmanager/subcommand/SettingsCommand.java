package eu.athelion.dailyrewards.commandmanager.subcommand;

import eu.athelion.dailyrewards.DailyRewardsPlugin;
import eu.athelion.dailyrewards.commandmanager.SubCommand;
import eu.athelion.dailyrewards.configuration.file.Lang;
import eu.athelion.dailyrewards.util.PermissionUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SettingsCommand implements SubCommand {
    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public Lang getDescription() {
        return Lang.SETTINGS_COMMAND_DESCRIPTION;
    }

    @Override
    public String getSyntax() {
        return "/reward settings";
    }

    @Override
    public String getPermission() {
        return PermissionUtil.Permission.SETTINGS_MENU.get();
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only in-game command!");
            return;
        }

        final Player player = (Player) sender;

        DailyRewardsPlugin.getMenuManager().openSettings(player);
    }
}
