package dev.revivalo.dailyrewards.hook;

import org.jetbrains.annotations.Nullable;

public interface IHook<T> {
    boolean isOn();

    @Nullable
    T getApi();
}
