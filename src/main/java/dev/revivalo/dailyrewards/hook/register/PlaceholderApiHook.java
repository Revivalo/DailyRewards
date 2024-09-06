package dev.revivalo.dailyrewards.hook.register;

import dev.revivalo.dailyrewards.hook.Hook;
import dev.revivalo.dailyrewards.hook.papiresolver.PAPIRegister;
import dev.revivalo.dailyrewards.util.VersionUtil;
import org.jetbrains.annotations.Nullable;

public class PlaceholderApiHook implements Hook<PlaceholderApiHook> {
    private boolean isHooked = false;

    @Override
    public void register() {
        if (VersionUtil.isLoaded("PlaceholderAPI")) {
            new PAPIRegister().register();
            isHooked = true;
        }
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Nullable
    @Override
    public PlaceholderApiHook getApi() {
        return isHooked ? this : null;
    }
}
