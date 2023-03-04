package dev.revivalo.dailyrewards.utils;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VersionUtil {
    public static boolean checkPlugin(String pluginName){
        return DailyRewardsPlugin.get().getPluginManager().getPlugin(pluginName) != null;
    }
}
