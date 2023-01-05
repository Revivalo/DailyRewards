package cz.revivalo.dailyrewards.files;

import cz.revivalo.dailyrewards.RewardType;
import cz.revivalo.dailyrewards.files.PlayerData;
import cz.revivalo.dailyrewards.managers.Cooldowns;
import cz.revivalo.dailyrewards.managers.MySQLManager;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class DataManager {
    private static boolean USING_MYSQL = false;

    public static void setValues(final UUID id, Object... data) {
        if (USING_MYSQL) {
            try {
                MySQLManager.updateCooldown(id, data);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            final PlayerData playerData = PlayerData.getConfig(id);
            for (int i = 0; i < data.length; i += 2){
                playerData.set("rewards." + data[i], data[i+1]);
            }
            playerData.save();
        }
    }

    public static long getLong(final UUID id, final RewardType type){
        if (USING_MYSQL) return MySQLManager.getRewardsCooldown(id, type);
        else return PlayerData.getConfig(id).getLong("rewards." + type);
    }

    public static Collection<RewardType> getAvailableRewards(final Player player){
        final Collection<RewardType> availableRewards = new HashSet<>();
        for (int i = 0; i <= 3; i++) {
            if (i == 0) {
                if (Cooldowns.isRewardAvailable(player, RewardType.DAILY)
                        && (player.hasPermission("dailyreward.daily")
                        || player.hasPermission("dailyreward.daily.premium"))) {
                    availableRewards.add(RewardType.DAILY);
                }
            } else if (i == 1) {
                if (Cooldowns.isRewardAvailable(player, RewardType.WEEKLY)
                        && (player.hasPermission("dailyreward.weekly")
                        || player.hasPermission("dailyreward.weekly.premium"))) {
                    availableRewards.add(RewardType.WEEKLY);
                }
            } else if (i == 2) {
                if (Cooldowns.isRewardAvailable(player, RewardType.MONTHLY)
                        && (player.hasPermission("dailyreward.monthly")
                        || player.hasPermission("dailyreward.monthly.premium"))) {
                    availableRewards.add(RewardType.MONTHLY);
                }
            }
        }
        return availableRewards;
    }

    public static boolean isUsingMysql(){return USING_MYSQL;}
    public static void setUsingMysql(boolean use){
        USING_MYSQL = use;
    }
}
