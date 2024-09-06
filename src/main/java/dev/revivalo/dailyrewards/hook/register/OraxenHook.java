package dev.revivalo.dailyrewards.hook.register;

import dev.revivalo.dailyrewards.hook.Hook;
import dev.revivalo.dailyrewards.util.VersionUtil;
import org.jetbrains.annotations.Nullable;

public class OraxenHook implements Hook<Void> {

    private boolean isHooked;

    @Override
    public void register() {
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
