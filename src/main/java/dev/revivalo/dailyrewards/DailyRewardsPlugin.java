package dev.revivalo.dailyrewards;

import dev.revivalo.dailyrewards.commandmanager.commands.RewardMainCommand;
import dev.revivalo.dailyrewards.commandmanager.commands.RewardsMainCommand;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.configuration.data.PlayerData;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.configuration.enums.Lang;
import dev.revivalo.dailyrewards.hooks.Hooks;
import dev.revivalo.dailyrewards.listeners.InventoryClickListener;
import dev.revivalo.dailyrewards.listeners.PlayerJoinQuitListener;
import dev.revivalo.dailyrewards.managers.MenuManager;
import dev.revivalo.dailyrewards.managers.database.MySQLManager;
import dev.revivalo.dailyrewards.managers.reward.RewardManager;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.updatechecker.UpdateChecker;
import dev.revivalo.dailyrewards.updatechecker.UpdateNotificator;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.utils.VersionUtils;
import io.github.g00fy2.versioncompare.Version;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class DailyRewardsPlugin extends JavaPlugin {
    /*

     */
    private final int RESOURCE_ID = 81780;

    public static DailyRewardsPlugin plugin;
    private static String latestVersion;

    private static ConsoleCommandSender console;

    private static RewardManager rewardManager;
    private static MenuManager menuManager;

    private PluginManager pluginManager;

    public static DailyRewardsPlugin get() {
        return DailyRewardsPlugin.plugin;
    }

    @Override
    public void onEnable() {
        setPlugin(this);

        setConsole(get().getServer().getConsoleSender());
        setPluginManager(getServer().getPluginManager());

        Hooks.hook();

        Config.reload();

        if (Config.UPDATE_CHECKER.asBoolean()) {
            new UpdateChecker(RESOURCE_ID).getVersion(pluginVersion -> {
                setLatestVersion(pluginVersion);

                final String actualVersion = get().getDescription().getVersion();
                final boolean isNewerVersion = new Version(pluginVersion).isHigherThan(actualVersion);

                get().getLogger().info(isNewerVersion
                        ? String.format("There is a new v%s update available (You are running v%s).\n" +
                                "Outdated versions are no longer supported, get the latest one here: " +
                                "https://www.spigotmc.org/resources/%%E2%%9A%%A1-daily-weekly-monthly-rewards-mysql-hex-colors-support-1-8-1-19-3.81780/",
                        pluginVersion, actualVersion)
                        : String.format("You are running the latest release (%s)", pluginVersion));


                VersionUtils.setLatestVersion(!isNewerVersion);
            });
        }

        MySQLManager.init();
        DailyRewardsPlugin.setRewardManager(new RewardManager());
        DailyRewardsPlugin.setMenuManager(new MenuManager());

        get().registerCommands();
        get().implementListeners();

        ConsoleCommandSender console = getConsole();

        console.sendMessage(" ");
        console.sendMessage(" ");
        console.sendMessage(" ");
        console.sendMessage(ChatColor.GOLD + "[DailyRewards] Update your version to ULTIMATE and remove limitations!");
        console.sendMessage(ChatColor.GOLD + "[DailyRewards] Get it from here: https://bit.ly/ultimate-rewards");
        console.sendMessage(" ");
        console.sendMessage(" ");
        console.sendMessage(" ");
    }

    @Override
    public void onLoad() {
        getServer().getOnlinePlayers().forEach(player -> UserHandler.addUser(new User(player, DataManager.getPlayerData(player))));
    }

    @Override
    public void onDisable() {
        PlayerData.removeConfigs();
    }

    private void registerCommands() {
        new RewardMainCommand().registerMainCommand(this, "reward");
        new RewardsMainCommand().registerMainCommand(this, "rewards");
    }

    private void implementListeners() {
        getPluginManager().registerEvents(InventoryClickListener.getInstance(), this);
        getPluginManager().registerEvents(PlayerJoinQuitListener.getInstance(), this);
        getPluginManager().registerEvents(UpdateNotificator.getInstance(), this);
    }


    public static boolean isPremium(final Player player, final RewardType type) {
        return player.hasPermission(String.format("dailyreward.%s.premium", type));
    }

    public void runDelayed(Runnable runnable, long delay) {
        getScheduler().runTaskLater(this, runnable, delay);
    }

    public void runAsync(Runnable runnable) {
        getScheduler().runTaskAsynchronously(this, runnable);
    }


    public <T> CompletableFuture<T> completableFuture(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runAsync(() -> {
            try {
                future.complete(callable.call());
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        });
        return future;
    }

    public BukkitScheduler getScheduler() {
        return getServer().getScheduler();
    }

    public static void setPlugin(DailyRewardsPlugin plugin) {
        DailyRewardsPlugin.plugin = plugin;
    }

    public static String getLatestVersion() {
        return latestVersion;
    }

    public static void setLatestVersion(String latestVersion) {
        DailyRewardsPlugin.latestVersion = latestVersion;
    }

    public static ConsoleCommandSender getConsole() {
        return console;
    }

    public static void setConsole(ConsoleCommandSender console) {
        DailyRewardsPlugin.console = console;
    }

    public static RewardManager getRewardManager() {
        return rewardManager;
    }

    public static void setRewardManager(RewardManager rewardManager) {
        DailyRewardsPlugin.rewardManager = rewardManager;
    }

    public static MenuManager getMenuManager() {
        return menuManager;
    }

    public static void setMenuManager(MenuManager menuManager) {
        DailyRewardsPlugin.menuManager = menuManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public void reloadPlugin() {
        Config.reload();
        Lang.reload();

        DailyRewardsPlugin.getRewardManager().loadRewards();
        DailyRewardsPlugin.getMenuManager().loadBackgroundFiller();
    }
}