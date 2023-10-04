package dev.revivalo.dailyrewards.hooks;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.listeners.AuthMeLoginListener;
import dev.revivalo.dailyrewards.utils.VersionUtils;
import org.jetbrains.annotations.Nullable;

public class AuthMeHook implements Hook<Void>{
    private final boolean isHooked;
    AuthMeHook(){
        isHooked = hook();
        if (isHooked) {
            DailyRewardsPlugin.get().getPluginManager().registerEvents(new AuthMeLoginListener(), DailyRewardsPlugin.get());
        }
    }

    private boolean hook(){
        return VersionUtils.isLoaded("AuthMe");
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
