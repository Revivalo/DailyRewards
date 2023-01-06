package cz.revivalo.dailyrewards.managers.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.configuration.data.DataManager;
import cz.revivalo.dailyrewards.configuration.enums.Config;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.*;
import java.util.Enumeration;
import java.util.UUID;

public class MySQLManager {

	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Rewards(id varchar(255), daily long, weekly long, monthly long);";
	private static final String SELECT = "SELECT %value% FROM Rewards WHERE id='%selector%';";
	private static final String INSERT = "INSERT INTO Rewards (%columns%) VALUES (%values%);";
	private static final String UPDATE = "UPDATE Rewards SET %values% WHERE id='%id%';";

	private static Connection connection;

	@SneakyThrows
	public static void init() {
		if (!Config.USE_MYSQL.asBoolean()) return;

		final HikariConfig config = new HikariConfig();
		config.setUsername(Config.MYSQL_USERNAME.asString());
		config.setPassword(Config.MYSQL_PASSWORD.asString());
		config.setDriverClassName("com.mysql.cj.jdbc.Driver");
		config.setJdbcUrl(String.format("jdbc:mysql://%s:3306/%s?connectTimeout=1000000&autoReconnect=YES",
				Config.MYSQL_IP.asString(),
				Config.MYSQL_DBNAME.asString()));

		final Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			final Driver driver = drivers.nextElement();
			if (!"com.mysql.cj.jdbc.Driver".equals(driver.getClass().getName())) continue;
			DriverManager.deregisterDriver(driver);
		}

		try (final HikariDataSource dataSource = new HikariDataSource(config);
			 final Connection dataSourceConnection = dataSource.getConnection()) {

			connection = dataSourceConnection;
			connection.prepareStatement(CREATE_TABLE).execute();
			DataManager.setUsingMysql(true);

			final File directory = new File(DailyRewards.getPlugin(DailyRewards.class).getDataFolder(), "userdata");
			final File[] directoryListing = directory.listFiles();
			if (directoryListing == null) return;

			for (File file : directoryListing) {
				final String fileName = file.getName();
				final String uuid = fileName.substring(0, fileName.length() - 4);
				if (MySQLManager.playerExists(uuid)) continue;

				final FileConfiguration data = YamlConfiguration.loadConfiguration(file);
				connection.prepareStatement(INSERT.replace("%columns%", "id, daily, weekly, monthly")
								.replace("%values%", String.format("'%s', '%d', '%d', '%d'",
										uuid, data.getLong("rewards.daily"), data.getLong("rewards.weekly"), data.getLong("rewards.monthly"))))
						.executeUpdate();
			}
		} catch (SQLException exception) {
			throw new RuntimeException(exception);
		}
	}

	@SneakyThrows
	public static void createPlayer(final String uuid) {
		if (MySQLManager.playerExists(uuid)) return;
		connection.prepareStatement(INSERT
				.replace("%columns%", "id, daily, weekly, monthly")
				.replace("%values%", uuid + ", 0, 0, 0"));
	}

	@SneakyThrows
	public static boolean playerExists(final String uuid) {
		return connection.prepareStatement(SELECT
						.replace("%value%", "daily")
						.replace("%selector%", uuid))
				.executeQuery()
				.next();
	}

	public static void updateCooldown(final UUID uuid, Object... values) throws SQLException {
		final StringBuilder builder = new StringBuilder();
		final int valuesLength = values.length;
		for (int i = 0; i < valuesLength; i += 2) {
			builder.append(values[i]).append("='").append(values[i + 1]).append("'").append((i == (valuesLength - 2) ? "" : ","));
		}
		connection.prepareStatement(UPDATE.replace("%values%", builder).replace("%id%", uuid.toString()))
				.executeUpdate();
	}

	public static long getRewardsCooldown(final UUID uuid, RewardType rewardType) {
		long cooldown;
		try {
			final ResultSet resultSet = connection.prepareStatement(SELECT
							.replace("%value%", rewardType.toString())
							.replace("%selector%", uuid.toString()))
					.executeQuery();

			if (!resultSet.next()) return 0;
			cooldown = resultSet.getLong(rewardType.toString());
			resultSet.close();

		} catch (SQLException exception) {
			return -1;
		}
		return cooldown;
	}
}