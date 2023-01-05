package cz.revivalo.dailyrewards.managers;

import cz.revivalo.dailyrewards.RewardType;
import cz.revivalo.dailyrewards.files.Config;
import cz.revivalo.dailyrewards.files.DataManager;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class Cooldowns {
    public static Cooldown getCooldown(final Player player, final RewardType type){
        long cd = DataManager.getLong(player.getUniqueId(), type) - System.currentTimeMillis();
        switch (type){
            case DAILY:
                return new Cooldown(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(cd),
                        TimeUnit.MILLISECONDS.toMinutes(cd) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(cd)),
                        TimeUnit.MILLISECONDS.toSeconds(cd) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(cd))), cd);
            case WEEKLY:
            case MONTHLY:
                return new Cooldown(String.format(Config.COOL_DOWN_FORMAT.asString().replace("%days%", "%02d").replace("%hours%", "%02d"), TimeUnit.MILLISECONDS.toDays(cd),
                        TimeUnit.MILLISECONDS.toHours(cd) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(cd))), cd);
            default: return new Cooldown("Error occurred", 0L);

        }
    }

    public static boolean isRewardAvailable(final Player player, final RewardType rewardType){
        return (DataManager.getLong(player.getUniqueId(), rewardType) - System.currentTimeMillis()) < 0;
    }


    public static void setCooldown(final Player player, RewardType type){
        DataManager.setValues(player.getUniqueId(), type.toString(), System.currentTimeMillis() + type.getCooldown());
    }
}
