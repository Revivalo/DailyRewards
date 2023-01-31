package cz.revivalo.dailyrewards;

import cz.revivalo.dailyrewards.commandmanager.commands.RewardMainCommand;
import cz.revivalo.dailyrewards.commandmanager.commands.RewardsMainCommand;
import cz.revivalo.dailyrewards.configuration.data.PlayerData;
import cz.revivalo.dailyrewards.configuration.enums.Config;
import cz.revivalo.dailyrewards.listeners.InventoryClickListener;
import cz.revivalo.dailyrewards.listeners.PlayerJoinListener;
import cz.revivalo.dailyrewards.managers.MenuManager;
import cz.revivalo.dailyrewards.managers.PlaceholderManager;
import cz.revivalo.dailyrewards.managers.database.MySQLManager;
import cz.revivalo.dailyrewards.managers.reward.RewardManager;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
import cz.revivalo.dailyrewards.updatechecker.UpdateChecker;
import cz.revivalo.dailyrewards.updatechecker.UpdateNotificator;
import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public final class DailyRewards extends JavaPlugin {
    /*
     */
    private final int SERVICE_ID = 12070;
    private final int RESOURCE_ID = 81780;

    @Getter @Setter
    public static DailyRewards plugin;

    @Getter @Setter
    private static RewardManager rewardManager;
    @Getter @Setter
    private static MenuManager menuManager;

    @Getter @Setter
    private PluginManager pluginManager;

    private final Collection<String> supportedPlugins = getDescription().getSoftDepend();
    @Getter @Setter
    public static boolean papiInstalled;
    @Getter @Setter
    public static boolean oraxenInstalled;
    @Getter @Setter
    public static boolean itemsAdderInstalled;
    @Getter @Setter
    public static boolean latestVersion;
    @Getter @Setter
    public static boolean hexSupported;

    @Override
    public void onEnable() {
        DailyRewards.setPlugin(this);
        setPluginManager(getServer().getPluginManager());
        final String serverVersion = Bukkit.getBukkitVersion();
        DailyRewards.setHexSupported(
                serverVersion.contains("6") ||
                        serverVersion.contains("7") ||
                        serverVersion.contains("8") ||
                        serverVersion.contains("9"));

        new Metrics(this, SERVICE_ID);
        new UpdateChecker(RESOURCE_ID).getVersion(pluginVersion -> {
            if (!Config.UPDATE_CHECKER.asBoolean()) return;

            final String actualVersion = this.getDescription().getVersion();
            final boolean versionMatches = actualVersion.equalsIgnoreCase(pluginVersion);

            this.getLogger().info(versionMatches ?
                    String.format("You are running the latest release (%s)", pluginVersion) :
                    String.format("There is a new v%s update available (You are running v%s).\n" +
                                    "Outdated versions are no longer supported, get the latest one here: " +
                                    "https://www.spigotmc.org/resources/%%E2%%9A%%A1-daily-weekly-monthly-rewards-mysql-hex-colors-support-1-8-1-19-3.81780/",
                            pluginVersion, actualVersion));
            DailyRewards.setLatestVersion(versionMatches);
        });
        this.registerSupportedPlugins();

        MySQLManager.init();
        DailyRewards.setRewardManager(new RewardManager());
        DailyRewards.setMenuManager(new MenuManager());

        this.registerCommands();
        this.implementListeners();
    }

    @Override
    public void onDisable() {
        PlayerData.removeConfigs();
    }

    private void registerCommands() {
        new RewardMainCommand().registerMainCommand(this, "reward");
        new RewardsMainCommand().registerMainCommand(this, "rewards");
    }

    /*private void registerCommands() {
        final RewardCommand rewardCommand = new RewardCommand();
        this.getDescription().getCommands().keySet()
                .forEach(string -> {
                    final PluginCommand command = this.getCommand(string);
                    if (command == null) return;

                    command.setExecutor(rewardCommand);
                });
    }*/

    private void implementListeners() {
        getPluginManager().registerEvents(InventoryClickListener.getInstance(), this);
        getPluginManager().registerEvents(PlayerJoinListener.getInstance(), this);
        getPluginManager().registerEvents(UpdateNotificator.getInstance(), this);
    }

    private void registerSupportedPlugins(){
        for (final String plugin : supportedPlugins){
            if (getPluginManager().getPlugin(plugin) == null) continue;
            switch (plugin) {
                case "PlaceholderAPI":
                    setPapiInstalled(new PlaceholderManager().register());
                    break;
                case "ItemsAdder":
                    setItemsAdderInstalled(true);
                    break;
                case "Oraxen":
                    setOraxenInstalled(true);
                    break;
            }
            Bukkit.getLogger().info("[DailyRewards] " + plugin + " has been successfully registered into supported plugins!");
        }
    }

    public static String isPremium(final Player player, final RewardType type) {
        return player.hasPermission(String.format("dailyreward.%s.premium", type)) ? "_PREMIUM" : "";
    }
}