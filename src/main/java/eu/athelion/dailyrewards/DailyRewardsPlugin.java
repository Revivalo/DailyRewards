package eu.athelion.dailyrewards;

import eu.athelion.dailyrewards.commandmanager.command.RewardMainCommand;
import eu.athelion.dailyrewards.commandmanager.command.RewardsMainCommand;
import eu.athelion.dailyrewards.configuration.ColorTextModifier;
import eu.athelion.dailyrewards.configuration.PlaceholderColorTextModifier;
import eu.athelion.dailyrewards.configuration.TextModifier;
import eu.athelion.dailyrewards.data.DataManager;
import eu.athelion.dailyrewards.data.PlayerData;
import eu.athelion.dailyrewards.configuration.file.Config;
import eu.athelion.dailyrewards.hook.HookManager;
import eu.athelion.dailyrewards.manager.MenuManager;
import eu.athelion.dailyrewards.manager.backend.MySQLManager;
import eu.athelion.dailyrewards.manager.reward.RewardManager;
import eu.athelion.dailyrewards.updatechecker.UpdateChecker;
import eu.athelion.dailyrewards.user.User;
import eu.athelion.dailyrewards.user.UserHandler;
import eu.athelion.dailyrewards.util.TextUtil;
import eu.athelion.dailyrewards.util.VersionUtil;
import io.github.g00fy2.versioncompare.Version;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.*;
import java.util.logging.Level;

@Setter
@Getter
public final class DailyRewardsPlugin extends JavaPlugin {
    private static final int RESOURCE_ID = 81780;

    private static DailyRewardsPlugin plugin;
    private static ExecutorService executorService;
    @Getter
    private static String latestVersion;
    @Getter
    private static ConsoleCommandSender console;
    @Getter
    private static RewardManager rewardManager;
    @Getter
    private static MenuManager menuManager;
    @Getter
    private static UserHandler userHandler;
    @Getter
    private static TextModifier textModifier;
    private PluginManager pluginManager;

    public static DailyRewardsPlugin get() {
        return DailyRewardsPlugin.plugin;
    }

    @Override
    public void onEnable() {
        setPlugin(this);
        executorService = Executors.newSingleThreadExecutor();

        setConsole(get().getServer().getConsoleSender());
        setPluginManager(getServer().getPluginManager());

        HookManager.hook();

        File langFolder = new File(getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        String[] languages = {
                "English", "Czech", "Chinese", "French", "Portuguese", "Romanian", "Ukrainian",
                "German", "Polish", "Russian", "Turkish", "Spanish", "Finnish",
        };

        for (String language : languages) {
            copyResource("lang/" + language + ".yml");
        }

        String[] menus = {"settings"};

        for (String menu : menus) {
            copyResource("menus/" + menu + ".yml");
        }

        if (HookManager.isHookEnabled(HookManager.getPlaceholderApiHook())) {
            DailyRewardsPlugin.get().getLogger().info("Using PAPI text modifier");
            textModifier = new PlaceholderColorTextModifier();
        } else textModifier = new ColorTextModifier();

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

        setRewardManager(new RewardManager());
        setMenuManager(new MenuManager());
        setUserHandler(new UserHandler());

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
    }

    @Override
    public void onLoad() {
        getServer().getOnlinePlayers().forEach(player -> UserHandler.addUser(new User(player, DataManager.getPlayerData(player))));
    }

    @Override
    public void onDisable() {
        PlayerData.removeConfigs();
        executorService.shutdown();
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
        if (command.startsWith("give")) {
            String[] args = command.replace("give ", "").split(" ");
            Player player = Bukkit.getPlayer(args[0]);
            Material material = Material.valueOf(args[1].toUpperCase());
            int amount = 1;
            if (args.length == 3) amount = Integer.parseInt(args[2]);
            player.getInventory().addItem(new ItemStack(material, amount));
            return;
        }
        getServer().dispatchCommand(getServer().getConsoleSender(), command);
    }

    public void consoleLog(String message) {
        getConsole().sendMessage(TextUtil.colorize(message));
    }

    public void runDelayed(Runnable runnable, long delay) {
        getScheduler().runTaskLater(this, runnable, delay);
    }

    @SuppressWarnings("unused")
    public void runSync(Runnable runnable) {
        getScheduler().runTask(this, runnable);
    }

    public void runAsync(Runnable runnable) {
        executorService.submit(runnable);
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

    public static boolean isWithinMainThread() {
        return Bukkit.isPrimaryThread();
    }

    public static void setPlugin(DailyRewardsPlugin plugin) {
        DailyRewardsPlugin.plugin = plugin;
    }

    public static void setLatestVersion(String latestVersion) {
        DailyRewardsPlugin.latestVersion = latestVersion;
    }

    public static void setConsole(ConsoleCommandSender console) {
        DailyRewardsPlugin.console = console;
    }

    public static void setRewardManager(RewardManager rewardManager) {
        DailyRewardsPlugin.rewardManager = rewardManager;
    }

    public static void setMenuManager(MenuManager menuManager) {
        DailyRewardsPlugin.menuManager = menuManager;
    }

    public static void setUserHandler(UserHandler userHandler) {
        DailyRewardsPlugin.userHandler = userHandler;
    }

    public void reloadPlugin() {
        Config.reload();

        DailyRewardsPlugin.getRewardManager().loadRewards();
        DailyRewardsPlugin.getMenuManager().loadBackgroundFiller();
    }
}