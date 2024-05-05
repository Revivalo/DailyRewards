package dev.revivalo.dailyrewards.commandmanager.subcommands;

import dev.revivalo.dailyrewards.commandmanager.SubCommand;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ImportCommand implements SubCommand {
    @Override
    public String getName() {
        return "import";
    }

    @Override
    public String getDescription() {
        return "Imports all local data to database";
    }

    @Override
    public String getSyntax() {
        return "/reward import";
    }

    @Override
    public String getPermission() {
        return PermissionUtils.Permission.ADMIN_PERMISSION.get();
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        DataManager.importToDatabase(sender);
    }
}
