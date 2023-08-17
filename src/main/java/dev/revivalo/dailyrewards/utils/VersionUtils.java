package dev.revivalo.dailyrewards.utils;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import io.github.g00fy2.versioncompare.Version;
import org.bukkit.Bukkit;

public class VersionUtils {
    private static boolean hexSupport;
    private static boolean oldVersion;
    private static boolean legacyVersion;
    public static boolean latestVersion;

    static {
        final String serverVersion = Bukkit.getBukkitVersion();
        Version version = new Version(serverVersion);
        setHexSupport(version.isAtLeast("1.16"));
        setOldVersion(version.isLowerThan("1.9"));
        setLegacyVersion(version.isLowerThan("1.13"));
    }

    public static boolean isLoaded(String pluginName){
        return DailyRewardsPlugin.get().getPluginManager().getPlugin(pluginName) != null;
    }

    public static boolean isHexSupport() {
        return hexSupport;
    }

    public static void setHexSupport(boolean hexSupport) {
        VersionUtils.hexSupport = hexSupport;
    }

    public static boolean isOldVersion() {
        return oldVersion;
    }

    public static void setOldVersion(boolean oldVersion) {
        VersionUtils.oldVersion = oldVersion;
    }

    public static boolean isLegacyVersion() {
        return legacyVersion;
    }

    public static void setLegacyVersion(boolean legacyVersion) {
        VersionUtils.legacyVersion = legacyVersion;
    }

    public static void setLatestVersion(boolean latestVersion) {
        VersionUtils.latestVersion = latestVersion;
    }

    public static boolean isLatestVersion() {
        return latestVersion;
    }
}
