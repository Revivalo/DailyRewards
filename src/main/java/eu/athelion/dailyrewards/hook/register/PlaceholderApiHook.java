package eu.athelion.dailyrewards.hook.register;

import eu.athelion.dailyrewards.hook.Hook;
import eu.athelion.dailyrewards.hook.papiresolver.PAPIRegister;
import eu.athelion.dailyrewards.util.VersionUtil;
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
