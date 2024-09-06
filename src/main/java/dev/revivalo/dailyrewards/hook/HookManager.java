package dev.revivalo.dailyrewards.hook;

import dev.revivalo.dailyrewards.hook.register.*;

import java.util.HashMap;
import java.util.Map;

public class HookManager {
    private static boolean authUsed = false;

    private static final Map<HookName, Hook<?>> hooks = new HashMap<>();

    public static void hook() {
        hooks.put(HookName.PLACEHOLDER_API, new PlaceholderApiHook());
        hooks.put(HookName.ORAXEN, new OraxenHook());
        hooks.put(HookName.ITEMS_ADDER, new ItemsAdderHook());
        hooks.put(HookName.AUTH_ME, new AuthMeHook());
        hooks.put(HookName.NEX_AUTH, new NexAuthHook());
        hooks.put(HookName.BSTATS, new BStatsHook());

        for (Hook<?> hook : hooks.values()) {
            hook.preRegister();
        }
    }

    public static <T> boolean isHookEnabled(Hook<T> hook) {
        return hook != null && hook.isOn();
    }

    public static boolean isAuthUsed() {
        return authUsed;
    }

    public static void setAuthUsed(boolean authUsed) {
        HookManager.authUsed = authUsed;
    }

    public static PlaceholderApiHook getPlaceholderApiHook() {
        return (PlaceholderApiHook) hooks.get(HookName.PLACEHOLDER_API);
    }

    public static BStatsHook getBstatsHook() {
        return (BStatsHook) hooks.get(HookName.BSTATS);
    }

    public static OraxenHook getOraxenHook() {
        return (OraxenHook) hooks.get(HookName.ORAXEN);
    }

    public static ItemsAdderHook getItemsAdderHook() {
        return (ItemsAdderHook) hooks.get(HookName.ITEMS_ADDER);
    }

    public static NexAuthHook getNexAuthHook() {
        return (NexAuthHook) hooks.get(HookName.NEX_AUTH);
    }

    public static AuthMeHook getAuthMeHook() {
        return (AuthMeHook) hooks.get(HookName.AUTH_ME);
    }

    private enum HookName {
        PLACEHOLDER_API,
        ORAXEN,
        ITEMS_ADDER,
        AUTH_ME,
        NEX_AUTH,
        BSTATS;
    }
}
