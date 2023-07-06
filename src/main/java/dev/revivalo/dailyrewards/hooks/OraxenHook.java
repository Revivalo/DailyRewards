package dev.revivalo.dailyrewards.hooks;

import dev.revivalo.dailyrewards.utils.VersionUtils;
import org.jetbrains.annotations.Nullable;

public class OraxenHook implements Hook<Void> {

    private final boolean isHooked;
    OraxenHook(){
        isHooked = VersionUtils.isLoaded("Oraxen");
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
