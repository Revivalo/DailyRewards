package dev.revivalo.dailyrewards.hooks;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.listeners.NexAuthPlayerJoinListener;
import dev.revivalo.dailyrewards.utils.VersionUtils;
import org.jetbrains.annotations.Nullable;

public class NexAuthHook implements Hook<Void>{
    private final boolean isHooked;
    NexAuthHook(){
        isHooked = hook();
        if (isHooked) {
            DailyRewardsPlugin.get().getPluginManager().registerEvents(new NexAuthPlayerJoinListener(), DailyRewardsPlugin.get());
        }
    }

    private boolean hook(){
        return VersionUtils.isLoaded("NexAuth");
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Override
    public @Nullable Void getApi() {
        return null;
    }
}
