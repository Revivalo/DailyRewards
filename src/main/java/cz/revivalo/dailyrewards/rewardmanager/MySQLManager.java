package cz.revivalo.dailyrewards.rewardmanager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.Objects;

public class MySQLManager {
    private final FileConfiguration cfg;

    private final String ip;
    private final String databaseName;
    private final String username;
    private final String password;

    private Connection connection;
    public Statement stmt;

    public MySQLManager(FileConfiguration cfg){
        this.cfg = cfg;

        ip = cfg.getString("mysql-ip");
        databaseName = cfg.getString("mysql-database-name");
        username = cfg.getString("mysql-username");
        password = cfg.getString("mysql-password");

        if (!Objects.requireNonNull(ip).equalsIgnoreCase("none")) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":3306/" + databaseName, username, password);
                stmt = connection.createStatement();
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS playerData (playerName TINYTEXT, dailyCooldown BIGINT, weeklyCooldown BIGINT, monthlyCooldown BIGINT)");
                connection.close();
                ResultSet res = stmt.executeQuery("SELECT typeOfBoost FROM activeBoosters WHERE typeOfBoost='fly'");
                if (!res.next()){
                    stmt.executeUpdate("INSERT INTO activeBoosters (typeOfBoost, activeTime) VALUES ('fly', 0)");
                }
                ResultSet res2 = stmt.executeQuery("SELECT typeOfBoost FROM activeBoosters WHERE typeOfBoost='mining'");
                if (!res2.next()){
                    stmt.executeUpdate("INSERT INTO activeBoosters (typeOfBoost, activeTime) VALUES ('mining', 0)");
                }
            } catch (SQLException ex) {
                Bukkit.getLogger().warning(ex.getMessage());
            }
        }
    }

    public void update(String nick, long cooldown) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":3306/" + databaseName, username, password);
        connection.close();
    }
}