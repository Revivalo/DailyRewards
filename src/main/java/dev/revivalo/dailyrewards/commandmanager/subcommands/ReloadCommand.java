package dev.revivalo.dailyrewards.commandmanager.subcommands;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.commandmanager.SubCommand;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand implements SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads a plugin's configuration";
    }

    @Override
    public String getSyntax() {
        return "/reward reload";
    }

    @Override
    public String getPermission() {
        return "dailyreward.manage";
    }

    @Override
    public List<String> getTabCompletion(CommandSender commandSender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Config.reload();
        Lang.reload();

        DailyRewardsPlugin.getMenuManager().loadBackgroundFiller();

        sender.sendMessage(Lang.RELOAD_MESSAGE.asColoredString());
    }
}
