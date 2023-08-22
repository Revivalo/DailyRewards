package dev.revivalo.dailyrewards.hooks;

public class Hooks {
    private static PlaceholderApiHook PLACEHOLDER_API_HOOK;
    private static BStatsHook BSTATS_HOOK;
    private static OraxenHook ORAXEN_HOOK;
    private static ItemsAdderHook ITEMS_ADDER_HOOK;


    public static void hook() {
        PLACEHOLDER_API_HOOK = new PlaceholderApiHook();
        BSTATS_HOOK = new BStatsHook();
        ORAXEN_HOOK = new OraxenHook();
        ITEMS_ADDER_HOOK = new ItemsAdderHook();
    }

    public static <T> boolean isHookEnabled(Hook<T> hook) {
        return hook != null && hook.isOn();
    }

    public static PlaceholderApiHook getPlaceholderApiHook() {
        return PLACEHOLDER_API_HOOK;
    }

    public static BStatsHook getBstatsHook() {
        return BSTATS_HOOK;
    }

    public static OraxenHook getOraxenHook() {
        return ORAXEN_HOOK;
    }

    public static ItemsAdderHook getItemsAdderHook() {
        return ITEMS_ADDER_HOOK;
    }
}
