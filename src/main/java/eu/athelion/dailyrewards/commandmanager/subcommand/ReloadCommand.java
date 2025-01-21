package eu.athelion.dailyrewards.commandmanager.subcommand;

import eu.athelion.dailyrewards.DailyRewardsPlugin;
import eu.athelion.dailyrewards.commandmanager.SubCommand;
import eu.athelion.dailyrewards.configuration.file.Lang;
import eu.athelion.dailyrewards.util.PermissionUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand implements SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public Lang getDescription() {
        return Lang.RELOAD_COMMAND_DESCRIPTION;
    }

    @Override
    public String getSyntax() {
        return "/reward reload";
    }

    @Override
    public String getPermission() {
        return PermissionUtil.Permission.RELOAD_PLUGIN.get();
    }

    @Override
    public List<String> getTabCompletion(CommandSender commandSender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        DailyRewardsPlugin.get().reloadPlugin();

        sender.sendMessage(Lang.RELOAD_MSG.asColoredString());
    }
}
