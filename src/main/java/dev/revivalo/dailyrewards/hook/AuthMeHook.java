package dev.revivalo.dailyrewards.hook;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.listener.AuthMeLoginListener;
import dev.revivalo.dailyrewards.util.VersionUtil;
import org.jetbrains.annotations.Nullable;

public class AuthMeHook implements IHook<Void> {
    private final boolean isHooked;
    AuthMeHook(){
        isHooked = hook();
        if (isHooked) {
            DailyRewardsPlugin.get().getPluginManager().registerEvents(new AuthMeLoginListener(), DailyRewardsPlugin.get());
        }
    }

    private boolean hook(){
        return VersionUtil.isLoaded("AuthMe");
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
