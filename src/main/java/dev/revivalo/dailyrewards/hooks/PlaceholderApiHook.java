package dev.revivalo.dailyrewards.hooks;

import dev.revivalo.dailyrewards.hooks.papiresolvers.PAPIRegister;
import dev.revivalo.dailyrewards.utils.VersionUtils;
import org.jetbrains.annotations.Nullable;

public class PlaceholderApiHook implements Hook<PlaceholderApiHook> {
    private boolean isHooked = false;

    PlaceholderApiHook() {
        tryToHook();
    }

    private void tryToHook() {
        if (VersionUtils.isLoaded("PlaceholderAPI")) {
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
