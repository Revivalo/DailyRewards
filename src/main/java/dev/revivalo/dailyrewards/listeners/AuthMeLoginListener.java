package dev.revivalo.dailyrewards.listeners;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.user.UserHandler;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AuthMeLoginListener implements Listener, LoginListener<LoginEvent> {
    @EventHandler
    public void onLogin(LoginEvent event) {
        DailyRewardsPlugin.getRewardManager().processAutoClaimForUser(UserHandler.getUser(event.getPlayer()));
    }
}
