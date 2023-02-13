package dev.revivalo.dailyrewards.hooks;

import org.jetbrains.annotations.Nullable;

public interface Hook<T> {
    boolean isOn();

    @Nullable
    T getApi();
}
