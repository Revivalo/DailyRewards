package dev.revivalo.dailyrewards.hooks;

import dev.revivalo.dailyrewards.utils.VersionUtils;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Hooks {
    @Getter private static PlaceholderApiHook PLACEHOLDER_API_HOOK;
    @Getter private static BStatsHook BSTATS_HOOK;
    @Getter private static OraxenHook ORAXEN_HOOK;
    @Getter private static ItemsAdderHook ITEMS_ADDER_HOOK;


    public static void hook(){
        if (VersionUtils.checkPlugin("PlaceholderAPI")) PLACEHOLDER_API_HOOK = new PlaceholderApiHook();
        BSTATS_HOOK = new BStatsHook();
        ORAXEN_HOOK = new OraxenHook();
        ITEMS_ADDER_HOOK = new ItemsAdderHook();
    }
}
