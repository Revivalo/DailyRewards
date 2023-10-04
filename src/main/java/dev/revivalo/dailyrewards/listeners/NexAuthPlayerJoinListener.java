package dev.revivalo.dailyrewards.listeners;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.user.UserHandler;
import org.bukkit.event.Listener;
import su.nexmedia.auth.api.event.AuthPlayerLoginEvent;

public class NexAuthPlayerJoinListener implements Listener, LoginListener<AuthPlayerLoginEvent> {

    @Override
    public void onLogin(AuthPlayerLoginEvent event) {
        DailyRewardsPlugin.getRewardManager().processAutoClaimForUser(UserHandler.getUser(event.getPlayer()));
    }
}
