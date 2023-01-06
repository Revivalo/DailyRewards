package cz.revivalo.dailyrewards;

import cz.revivalo.dailyrewards.commands.RewardCommand;
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
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Logger;

public final class DailyRewards extends JavaPlugin {

	@Getter @Setter
	public static boolean papiEnabled;
	@Getter @Setter
	public static boolean latestVersion;
	@Getter @Setter
	public static boolean hexSupported;
	@Getter @Setter
	public static DailyRewards plugin;

	@Getter @Setter
	private static RewardManager rewardManager;
	@Getter @Setter
	private static MenuManager guiManager;

	@Getter private final Logger logger = this.getLogger();

	@Override
	public void onEnable() {
		DailyRewards.setPlugin(this);

		final String serverVersion = Bukkit.getBukkitVersion();
		DailyRewards.setHexSupported(
				serverVersion.contains("6") ||
						serverVersion.contains("7") ||
						serverVersion.contains("8") ||
						serverVersion.contains("9"));

		MySQLManager.init();
		new Metrics(this, 12070);
		new UpdateChecker(81780).getVersion(pluginVersion -> {
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

		DailyRewards.setRewardManager(new RewardManager());
		DailyRewards.setGuiManager(new MenuManager());
		this.registerCommands();
		this.implementListeners();

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new PlaceholderManager().register();
			DailyRewards.setPapiEnabled(true);
			return;
		}
		logger.warning("Could not find PlaceholderAPI, placeholders will not work");
	}

	@Override
	public void onDisable() {
		PlayerData.removeConfigs();
	}

	private void registerCommands() {
		Arrays.asList("reward", "rewards")
				.forEach(string -> {
					final PluginCommand command = this.getCommand(string);
					if (command == null) return;

					command.setExecutor(new RewardCommand());
				});
	}

	private void implementListeners() {
		final PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(InventoryClickListener.getInstance(), this);
		pluginManager.registerEvents(PlayerJoinListener.getInstance(), this);
		pluginManager.registerEvents(UpdateNotificator.getInstance(), this);
	}

	public static String isPremium(final Player player, final RewardType type) {
		return player.hasPermission(String.format("dailyreward.%s.premium", type)) ? "_PREMIUM" : "";
	}
}
