package dev.revivalo.dailyrewards.hook;

import dev.revivalo.dailyrewards.hook.papiresolver.PAPIRegister;
import dev.revivalo.dailyrewards.util.VersionUtil;
import org.jetbrains.annotations.Nullable;

public class PlaceholderApiHook implements IHook<PlaceholderApiHook> {
    private boolean isHooked = false;

    PlaceholderApiHook() {
        tryToHook();
    }

    private void tryToHook() {
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
