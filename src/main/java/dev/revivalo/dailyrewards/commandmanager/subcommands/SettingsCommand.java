package dev.revivalo.dailyrewards.commandmanager.subcommands;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.commandmanager.SubCommand;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SettingsCommand implements SubCommand {
    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public String getDescription() {
        return "Opens a settings menu";
    }

    @Override
    public String getSyntax() {
        return "/reward settings";
    }

    @Override
    public String getPermission() {
        return PermissionUtils.Permission.SETTINGS_MENU.get();
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
