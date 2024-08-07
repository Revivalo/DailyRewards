package dev.revivalo.dailyrewards.hook;

import dev.revivalo.dailyrewards.util.VersionUtil;
import org.jetbrains.annotations.Nullable;

public class OraxenHook implements IHook<Void> {

    private final boolean isHooked;
    OraxenHook(){
        isHooked = VersionUtil.isLoaded("Oraxen");
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
