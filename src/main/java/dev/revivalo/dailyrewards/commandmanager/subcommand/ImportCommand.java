package dev.revivalo.dailyrewards.commandmanager.subcommand;

import dev.revivalo.dailyrewards.commandmanager.SubCommand;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.util.PermissionUtil;
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
        return PermissionUtil.Permission.ADMIN_PERMISSION.get();
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
