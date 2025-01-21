package eu.athelion.dailyrewards.hook.register;

import eu.athelion.dailyrewards.hook.Hook;
import eu.athelion.dailyrewards.util.VersionUtil;
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
