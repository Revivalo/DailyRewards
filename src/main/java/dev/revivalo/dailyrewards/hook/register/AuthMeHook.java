package dev.revivalo.dailyrewards.hook.register;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.hook.Hook;
import dev.revivalo.dailyrewards.hook.HookManager;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.util.VersionUtil;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.Nullable;

public class AuthMeHook implements Hook<Void> {
    private boolean isHooked;

    private boolean hook(){
        return VersionUtil.isLoaded("AuthMe");
    }

    @Override
    public void register() {
        isHooked = hook();
        if (isHooked) {
            DailyRewardsPlugin.get().registerListeners(new AuthMeLoginListener());
            HookManager.setAuthUsed(true);
        }
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
