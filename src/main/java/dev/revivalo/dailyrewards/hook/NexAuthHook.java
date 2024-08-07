package dev.revivalo.dailyrewards.hook;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.listener.NexAuthPlayerJoinListener;
import dev.revivalo.dailyrewards.util.VersionUtil;
import org.jetbrains.annotations.Nullable;

public class NexAuthHook implements IHook<Void> {
    private final boolean isHooked;
    NexAuthHook(){
        isHooked = hook();
        if (isHooked) {
            DailyRewardsPlugin.get().getPluginManager().registerEvents(new NexAuthPlayerJoinListener(), DailyRewardsPlugin.get());
        }
    }

    private boolean hook(){
        return VersionUtil.isLoaded("NexAuth");
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
