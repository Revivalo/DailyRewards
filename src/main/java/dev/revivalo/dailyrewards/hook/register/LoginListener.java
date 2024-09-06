package dev.revivalo.dailyrewards.hook.register;

import org.bukkit.event.Listener;

public abstract class LoginListener<T> implements Listener {
    @SuppressWarnings("unused")
    protected abstract void onLogin(T event);
}
