package dev.revivalo.dailyrewards.managers.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.configuration.enums.Config;
import dev.revivalo.dailyrewards.managers.reward.RewardType;
import dev.revivalo.dailyrewards.utils.VersionUtils;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MySQLManager {

	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Rewards(id varchar(255), daily long, weekly long, monthly long);";
	private static final String SELECT = "SELECT %value% FROM Rewards WHERE id='%selector%';";
	private static final String INSERT = "INSERT INTO Rewards (%columns%) VALUES (%values%);";
	private static final String UPDATE = "UPDATE Rewards SET %values% WHERE id='%id%';";

	private static HikariDataSource dataSource;

	public static void init(){
		if (!Config.USE_MYSQL.asBoolean()) return;

		String username = Config.MYSQL_USERNAME.asString();
		String password = Config.MYSQL_PASSWORD.asString();
		HikariConfig config = new HikariConfig();

		if (VersionUtils.isLegacyVersion()) config.setDriverClassName("com.mysql.jdbc.Driver");
		else config.setDriverClassName("com.mysql.cj.jdbc.Driver");

		config.setJdbcUrl("jdbc:mysql://" + Config.MYSQL_IP.asString() + ":" + Config.MYSQL_PORT.asString() + "/" + Config.MYSQL_DBNAME.asString() +
				"?testConnectOnCheckout=true" +
				"&idleConnectionTestPeriod=3600" +
				"&allowReconnect=true" +
				"&autoReconnect=true");

		config.setUsername(username);
		config.setPassword(password);
		config.setIdleTimeout(0);

		dataSource = new HikariDataSource(config);

		try (Connection connection = getConnection()) {
			connection.prepareStatement(CREATE_TABLE).execute();
			DataManager.setUsingMysql(true);
			File dir = new File(DailyRewardsPlugin.getPlugin(DailyRewardsPlugin.class).getDataFolder(), "userdata");
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File file : directoryListing) {
					ConfigurationSection rewardsConfigurationSection = YamlConfiguration.loadConfiguration(file).getConfigurationSection("rewards");
					final String fileName = file.getName();
					final String uuid = fileName.substring(0, fileName.length() - 4);
					if (!playerExists(uuid))
						connection.prepareStatement(INSERT
								.replace("%columns%", "id, daily, weekly, monthly")
								.replace(
										"%values%",
										"'"
												+ uuid + "', '"
												+ rewardsConfigurationSection.getLong("daily")
												+ "', '"
												+ rewardsConfigurationSection.getLong("weekly")
												+ "', '"
												+ rewardsConfigurationSection.getLong("monthly")
												+ "'"))
								.executeUpdate();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static void createPlayer(final String uuid) {
		if (MySQLManager.playerExists(uuid)) return;
		final long currentTimeInMillis = System.currentTimeMillis();
		try (Connection connection = getConnection()) {
			connection.prepareStatement(INSERT
					.replace("%columns%", "id, daily, weekly, monthly")
					.replace("%values%", uuid + ", " +
							(Config.DAILY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean() ? 0 : currentTimeInMillis + RewardType.DAILY.getCooldown()) + ", " +
							(Config.WEEKLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean() ? 0 : currentTimeInMillis + RewardType.WEEKLY.getCooldown()) + ", " +
							(Config.MONTHLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean() ? 0 : currentTimeInMillis + RewardType.MONTHLY.getCooldown())))
					.execute();
		} catch (SQLException ex) {
			DailyRewardsPlugin.get().getLogger().warning("Failed to create player " + uuid + "!\n" + ex.getLocalizedMessage());
		}
	}

	public static boolean playerExists(final String uuid) {
		try (Connection connection = getConnection()) {
			return connection.prepareStatement(SELECT
							.replace("%value%", "daily")
							.replace("%selector%", uuid))
					.executeQuery()
					.next();
		} catch (SQLException ex) {
			DailyRewardsPlugin.get().getLogger().warning("Failed to check if player " + uuid + " exists!\n" + ex.getLocalizedMessage());
			return false;
		}
	}

	public static void updateCooldown(final UUID uuid, Map<String, Object> data) throws SQLException {
		final StringBuilder builder = new StringBuilder();
		Iterator<String> keys = data.keySet().iterator();
		while (keys.hasNext()){
			String key = keys.next();
			Object value = data.get(key);
			builder.append(key)
					.append("='")
					.append(value)
					.append("'")
					.append(keys.hasNext()
							? "?"
							: "");
		}
		try (Connection connection = getConnection()) {
			connection.prepareStatement(UPDATE
					.replace("%values%", builder)
					.replace("%id%", uuid.toString())
			).executeUpdate();
		} catch (SQLException ex) {
			DailyRewardsPlugin.get().getLogger().warning("Failed to update cooldown for " + uuid + "!\n" + ex.getLocalizedMessage());
		}
	}

	public static long getRewardsCooldown(final UUID uuid, RewardType rewardType) {
		long cooldown;
		try (Connection connection = getConnection()) {
			final ResultSet resultSet = connection.prepareStatement(SELECT
							.replace("%value%", rewardType.toString())
							.replace("%selector%", uuid.toString())
					).executeQuery();

			if (!resultSet.next()) return 0;
			cooldown = resultSet.getLong(rewardType.toString());
		} catch (SQLException exception) {
			return -1;
		}
		return cooldown;
	}

	@SneakyThrows(SQLException.class)
	private static Connection getConnection() {
		if (dataSource == null) {
			throw new SQLException("Unable to get a connection from the pool because the dataSource is null");
		}
		Connection connection = dataSource.getConnection();
		if (connection == null) {
			throw new SQLException("Unable to get a connection from the pool.");
		}
		return connection;
	}
}