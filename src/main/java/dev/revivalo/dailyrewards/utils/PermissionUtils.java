package dev.revivalo.dailyrewards.utils;

import dev.revivalo.dailyrewards.user.User;
import org.bukkit.command.CommandSender;

public class PermissionUtils {
    public static boolean hasPermission(CommandSender commandSender, Permission permission){
        if (commandSender.isOp())
            return true;

        else if (commandSender.hasPermission(Permission.ADMIN_PERMISSION.get()))
            return true;

        else
            return commandSender.hasPermission(permission.get());
    }

    public static boolean hasPermission(User user, Permission permission) {
        return hasPermission(user.getPlayer(), permission);
    }

    public enum Permission {
        ADMIN_PERMISSION("dailyreward.admin"),
        RELOAD_PLUGIN("dailyreward.reload"),
        OPENS_MAIN_REWARD_MENU("dailyreward.use"),
        SETTINGS_MENU("dailyreward.settings"),
        JOIN_NOTIFICATION_SETTING("dailyreward.settings.joinnotification"),
        AUTO_CLAIM_SETTING("dailyreward.settings.autoclaim"),
        REQUIRED_PLAYTIME_BYPASS("dailyreward.requiredplaytime.bypass"),
        CLAIM_REWARDS("dailyreward.claim"),
        RESET_FOR_OTHERS("dailyreward.manage"),
        HELP("dailyreward.help");

        private final String permission;
        Permission(String permission) {
            this.permission = permission;
        }

        public String get() {
            return permission;
        }
    }
}
