package dev.revivalo.dailyrewards.utils;

import org.bukkit.command.CommandSender;

public class PermissionUtils {
    public static boolean hasPermission(CommandSender commandSender, String permission) {
        if (permission == null || commandSender == null) {
            return true;
        }

        if (commandSender.isOp()) {
            return true;
        }

        else if (commandSender.hasPermission(Permission.ADMIN_PERMISSION.get())) {
            return true;
        }

        else {
            return commandSender.hasPermission(permission);
        }
    }

    public static boolean hasPermission(CommandSender commandSender, Permission permission){
        return hasPermission(commandSender, permission == null ? null : permission.get());
    }

    public enum Permission {
        ADMIN_PERMISSION("dailyreward.admin"),
        RELOAD_PLUGIN("dailyreward.reload"),
        OPENS_MAIN_REWARD_MENU("dailyreward.use"),
        SETTINGS_MENU("dailyreward.settings"),
        JOIN_NOTIFICATION_SETTING("dailyreward.settings.joinNotification"),
        AUTO_CLAIM_SETTING("dailyreward.settings.autoClaim"),
        REQUIRED_PLAYTIME_BYPASS("dailyreward.requiredPlaytime.bypass"),
        REQUIRED_FREE_SLOTS_BYPASS("dailyreward.requiredFreeSlots.bypass"),
        DISABLED_WORLDS_BYPASS("dailyreward.disabledWorlds.bypass"),
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
