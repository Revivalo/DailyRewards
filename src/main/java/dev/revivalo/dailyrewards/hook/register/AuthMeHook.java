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
            DailyRewardsPlugin.get().registerListeners(new AuthMeLoginListener());
            HookManager.setAuthUsed(true);
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

    private static class AuthMeLoginListener extends LoginListener<LoginEvent> {
        @Override
        @EventHandler
        public void onLogin(LoginEvent event) {
            DailyRewardsPlugin.getRewardManager().processAutoClaimForUser(UserHandler.getUser(event.getPlayer()));
        }
    }
}
