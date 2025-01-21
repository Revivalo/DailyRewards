package eu.athelion.dailyrewards.hook.register;

import eu.athelion.dailyrewards.DailyRewardsPlugin;
import eu.athelion.dailyrewards.hook.Hook;
import eu.athelion.dailyrewards.hook.HookManager;
import eu.athelion.dailyrewards.user.UserHandler;
import eu.athelion.dailyrewards.util.VersionUtil;
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
            DailyRewardsPlugin.getUserHandler().getAutoClaimTask().addUser(UserHandler.getUser(event.getPlayer()));
            DailyRewardsPlugin.getUserHandler().getJoinNotificationTask().addUser(UserHandler.getUser(event.getPlayer()));
        }
    }
}
