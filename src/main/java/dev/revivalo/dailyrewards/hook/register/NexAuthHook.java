package dev.revivalo.dailyrewards.hook.register;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.hook.Hook;
import dev.revivalo.dailyrewards.hook.HookManager;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.util.VersionUtil;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.auth.api.event.AuthPlayerLoginEvent;

public class NexAuthHook implements Hook<Void> {
    private boolean isHooked;

    private boolean hook(){
        return VersionUtil.isLoaded("NexAuth");
    }

    @Override
    public void register() {
        isHooked = hook();
        if (isHooked) {
            DailyRewardsPlugin.get().registerListeners(new NexAuthPlayerJoinListener());
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

    private static class NexAuthPlayerJoinListener extends LoginListener<AuthPlayerLoginEvent> {
        @Override
        @EventHandler
        public void onLogin(AuthPlayerLoginEvent event) {
            DailyRewardsPlugin.getRewardManager().processAutoClaimForUser(UserHandler.getUser(event.getPlayer()));
        }
    }
}
