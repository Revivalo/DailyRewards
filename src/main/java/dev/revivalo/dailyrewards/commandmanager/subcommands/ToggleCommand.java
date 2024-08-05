package dev.revivalo.dailyrewards.commandmanager.subcommands;

import dev.revivalo.dailyrewards.commandmanager.SubCommand;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.managers.Setting;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.PermissionUtils;
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
    public String getDescription() {
        return "Toggles certain setting";
    }

    @Override
    public String getSyntax() {
        return "/reward toggle <setting>";
    }

    @Override
    public String getPermission() {
        return PermissionUtils.Permission.SETTINGS_MENU.get();
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
            sender.sendMessage(Lang.COMMAND_USAGE.asColoredString());
            return;
        }

        try {
            Setting setting = Setting.valueOf(args[0].toUpperCase(Locale.ENGLISH));
            final Player player = (Player) sender;
            if (!PermissionUtils.hasPermission(player, "dailyreward.settings." + setting.getTag())) {
                player.sendMessage(Lang.PERMISSION_MSG.asColoredString());
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
