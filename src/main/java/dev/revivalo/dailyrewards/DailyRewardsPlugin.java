package dev.revivalo.dailyrewards;

import dev.revivalo.dailyrewards.commandmanager.command.RewardMainCommand;
import dev.revivalo.dailyrewards.commandmanager.command.RewardsMainCommand;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.configuration.data.PlayerData;
import dev.revivalo.dailyrewards.configuration.file.Config;
import dev.revivalo.dailyrewards.hook.HookManager;
import dev.revivalo.dailyrewards.manager.MenuManager;
import dev.revivalo.dailyrewards.manager.backend.MySQLManager;
import dev.revivalo.dailyrewards.manager.reward.RewardManager;
import dev.revivalo.dailyrewards.updatechecker.UpdateChecker;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.util.VersionUtil;
import io.github.g00fy2.versioncompare.Version;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Level;

public final class DailyRewardsPlugin extends JavaPlugin {
    /*

     */
    private final int RESOURCE_ID = 81780;

    private static DailyRewardsPlugin plugin;
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

        HookManager.hook();

        File langFolder = new File(getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        String[] languages = {
                "English", "Czech", "Chinese", "French", "Portuguese",
                "German", "Polish", "Russian", "Turkish", "Spanish"
        };

        for (String language : languages) {
            copyResource("lang/" + language + ".yml");
        }

        Config.reload();

        if (Config.UPDATE_CHECKER.asBoolean()) {
            new UpdateChecker(RESOURCE_ID).getVersion(pluginVersion -> {
                setLatestVersion(pluginVersion);

                final String actualVersion = getDescription().getVersion();
                Version version = new Version(pluginVersion);
                final boolean isNewerVersion = version.isHigherThan(actualVersion);
                final boolean isDevelopmentBuild = version.isLowerThan(actualVersion);

                if (isDevelopmentBuild) {
                    getLogger().info(String.format("You are running a development build (%s).", actualVersion));
                } else {

                    if (isNewerVersion) {
                        getLogger().info(String.format("There is a new v%s update available (You are running v%s).\n" +
                                "Outdated versions are no longer supported, get the latest one here: " +
                                "https://www.spigotmc.org/resources/%%E2%%9A%%A1-daily-weekly-monthly-rewards-mysql-hex-colors-support-1-8-1-19-3.81780/", pluginVersion, actualVersion));
                    } else {
                        getLogger().info(String.format("You are running the latest release (%s).", pluginVersion));
                    }
                }

                VersionUtil.setLatestVersion(!isNewerVersion);
            });
        }

        MySQLManager.init();
        DailyRewardsPlugin.setRewardManager(new RewardManager());
        DailyRewardsPlugin.setMenuManager(new MenuManager());

        registerCommands();

        ConsoleCommandSender console = getConsole();

        console.sendMessage(" ");
        console.sendMessage(" ");
        console.sendMessage(" ");
        console.sendMessage(ChatColor.GOLD + "[DailyRewards] Unleash the full potential of DailyRewards by upgrading to ULTIMATE!");
        console.sendMessage(ChatColor.GOLD + "[DailyRewards] Get it from https://bit.ly/ultimate-rewards");
        console.sendMessage(" ");
        console.sendMessage(" ");
        console.sendMessage(" ");

        new UserHandler();
    }

    @Override
    public void onLoad() {
        getServer().getOnlinePlayers().forEach(player -> UserHandler.addUser(new User(player, DataManager.getPlayerData(player))));
    }

    @Override
    public void onDisable() {
        PlayerData.removeConfigs();
    }

    private void copyResource(String resourcePath) {
        File outFile = new File(getDataFolder(), resourcePath);
        if (!outFile.exists()) {
            try (InputStream in = getResource(resourcePath)) {
                if (in == null) {
                    getLogger().log(Level.SEVERE, "Resource " + resourcePath + " not found in the plugin JAR!");
                    return;
                }
                outFile.getParentFile().mkdirs();
                Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                getLogger().log(Level.INFO, "Resource " + resourcePath + " successfully copied.");
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Failed to copy resource " + resourcePath, e);
            }
        }
    }

    public void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private void registerCommands() {
        new RewardMainCommand().registerMainCommand(this, "reward");
        new RewardsMainCommand().registerMainCommand(this, "rewards");

        new RewardMainCommand().registerMainCommand(this, "dreward");
        new RewardsMainCommand().registerMainCommand(this, "drewards");
    }

    public void executeCommandAsConsole(String command) {
        getServer().dispatchCommand(getServer().getConsoleSender(), command);
    }

    public void runDelayed(Runnable runnable, long delay) {
        getScheduler().runTaskLater(this, runnable, delay);
    }

    @SuppressWarnings("unused")
    public void runSync(Runnable runnable) {
        getScheduler().runTask(this, runnable);
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

        DailyRewardsPlugin.getRewardManager().loadRewards();
        DailyRewardsPlugin.getMenuManager().loadBackgroundFiller();
    }
}