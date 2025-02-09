package eu.athelion.dailyrewards.commandmanager.subcommand;

import eu.athelion.dailyrewards.commandmanager.SubCommand;
import eu.athelion.dailyrewards.configuration.file.Lang;
import eu.athelion.dailyrewards.manager.Setting;
import eu.athelion.dailyrewards.user.User;
import eu.athelion.dailyrewards.user.UserHandler;
import eu.athelion.dailyrewards.util.PermissionUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ToggleCommand implements SubCommand {
    @Override
    public String getName() {
        return "toggle";
    }

    @Override
    public Lang getDescription() {
        return Lang.TOGGLE_COMMAND_DESCRIPTION;
    }

    @Override
    public String getSyntax() {
        return "/reward toggle <setting>";
    }

    @Override
    public String getPermission() {
        return PermissionUtil.Permission.SETTINGS_MENU.get();
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        if (args.length == 2) {
            return Arrays.stream(Setting.values()).map(Setting::name).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only in-game command!");
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(Lang.COMMAND_USAGE.asColoredString().replace("%usage%", getSyntax()));
            return;
        }

        try {
            Setting setting = Setting.valueOf(args[0].toUpperCase(Locale.ENGLISH));
            final Player player = (Player) sender;
            if (!PermissionUtil.hasPermission(player, "dailyreward.settings." + setting.getTag())) {
                player.sendMessage(Lang.INSUFFICIENT_PERMISSION.asColoredString()
                        .replace("%permission%", "dailyreward.settings." + setting.getTag()));
                return;
            }
            User user = UserHandler.getUser(player);
            if (user.toggleSetting(setting, !user.hasSettingEnabled(setting))) {
                player.sendMessage(Lang.SETTING_ENABLED.asColoredString().replace("%setting%", setting.getName().asColoredString()));
            } else {
                player.sendMessage(Lang.SETTING_DISABLED.asColoredString().replace("%setting%", setting.getName().asColoredString()));
            }
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(Lang.UNAVAILABLE_SETTING.asColoredString());
        }
    }
}
