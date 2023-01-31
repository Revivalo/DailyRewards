package cz.revivalo.dailyrewards.managers.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.configuration.data.DataManager;
import cz.revivalo.dailyrewards.configuration.enums.Config;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MySQLManager {

	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Rewards(id varchar(255), daily long, weekly long, monthly long);";
	private static final String SELECT = "SELECT %value% FROM Rewards WHERE id='%selector%';";
	private static final String INSERT = "INSERT INTO Rewards (%columns%) VALUES (%values%);";
	private static final String UPDATE = "UPDATE Rewards SET %values% WHERE id='%id%';";

	private static Connection connection;

	@SneakyThrows
	public static void init(){
		if (!Config.USE_MYSQL.asBoolean()) return;

		String username = Config.MYSQL_USERNAME.asString();
		String password = Config.MYSQL_PASSWORD.asString();
		HikariConfig config = new HikariConfig();
		config.setDriverClassName("com.mysql.cj.jdbc.Driver");

		config.setJdbcUrl("jdbc:mysql://" + Config.MYSQL_IP.asString() + ":3306/" + Config.MYSQL_DBNAME.asString() +
				"?idleConnectionTestPeriod=3600" +
				"&testConnectionOnCheckin=true" +
				"&allowReconnect=true" +
				"&autoReconnect=true");
		config.setUsername(username);
		config.setPassword(password);

		HikariDataSource ds = new HikariDataSource(config);

		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (driver.getClass().getName().equals("com.mysql.cj.jdbc.Driver")) {
				try {
					DriverManager.deregisterDriver(driver);
				} catch (SQLException e) {
					throw new SQLException();
				}
			}
		}
		try {
			connection = ds.getConnection();
			connection.prepareStatement(CREATE_TABLE).execute();
			DataManager.setUsingMysql(true);
			File dir = new File(DailyRewards.getPlugin(DailyRewards.class).getDataFolder(), "userdata");
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

	@SneakyThrows
	public static void createPlayer(final String uuid) {
		if (MySQLManager.playerExists(uuid)) return;
		final long currentTimeInMillis = System.currentTimeMillis();
		connection.prepareStatement(INSERT
				.replace("%columns%", "id, daily, weekly, monthly")
				.replace("%values%", uuid + ", " +
						(Config.DAILY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean() ? 0 : currentTimeInMillis + RewardType.DAILY.getCooldown()) + ", " +
						(Config.WEEKLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean() ? 0 : currentTimeInMillis + RewardType.WEEKLY.getCooldown()) + ", " +
						(Config.MONTHLY_AVAILABLE_AFTER_FIRST_JOIN.asBoolean() ? 0 : currentTimeInMillis + RewardType.MONTHLY.getCooldown())));
	}

	@SneakyThrows
	public static boolean playerExists(final String uuid) {
		return connection.prepareStatement(SELECT
						.replace("%value%", "daily")
						.replace("%selector%", uuid))
				.executeQuery()
				.next();
	}

	public static void updateCooldown(final UUID uuid, Map<String, Object> data) throws SQLException {
		final StringBuilder builder = new StringBuilder();
		Iterator<String> keys = data.keySet().iterator();
		while (keys.hasNext()){
		//for (Map.Entry<String, Object> entry : data.entrySet()) {
			String key = keys.next();
			Object value = data.get(keys.next());
			builder.append(key)
					.append("='")
					.append(value)
					.append("'")
					.append(keys.hasNext()
							? ""
							: ",");
		}
		connection.prepareStatement(UPDATE
						.replace("%values%", builder)
						.replace("%id%", uuid.toString()))
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