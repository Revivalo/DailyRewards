package dev.revivalo.dailyrewards.hooks;

import dev.revivalo.dailyrewards.utils.VersionUtil;
import org.jetbrains.annotations.Nullable;

public class OraxenHook implements Hook<Void> {

    private final boolean isHooked;
    OraxenHook(){
        isHooked = VersionUtil.checkPlugin("Oraxen");
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
