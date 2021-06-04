package cz.revivalo.dailyrewards.rewardmanager;

import cz.revivalo.dailyrewards.lang.Lang;
import cz.revivalo.dailyrewards.playerconfig.PlayerConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Cooldowns {

    public String getCooldown(Player p, String type, boolean formated){
        FileConfiguration data = PlayerConfig.getConfig(p);
        long cd = data.getLong("rewards." + type) - System.currentTimeMillis();
        if (formated){
            switch (type){
                case "daily":
                    return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(cd),
                            TimeUnit.MILLISECONDS.toMinutes(cd) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(cd)),
                            TimeUnit.MILLISECONDS.toSeconds(cd) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(cd)));
                case "weekly":
                case "monthly":
                    return String.format(Lang.COOLDOWNFORMAT.content(p).replace("%days%", "%02d").replace("%hours%", "%02d"), TimeUnit.MILLISECONDS.toDays(cd),
                            TimeUnit.MILLISECONDS.toHours(cd) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(cd)));

            }
        } else {
            return String.valueOf(data.getLong("rewards." + type) - System.currentTimeMillis());
        }
        return null;
    }

    public void set(Player p){
        FileConfiguration data = PlayerConfig.getConfig(p);
        if (!data.isConfigurationSection("rewards")) {
            long currentTime = System.currentTimeMillis();
            Objects.requireNonNull(data).set("rewards.daily", currentTime + 86400000);
            Objects.requireNonNull(data).set("rewards.weekly", currentTime + 604800000);
            Objects.requireNonNull(data).set("rewards.monthly", currentTime + Long.parseLong("2592000000"));
            PlayerConfig.getConfig(p).save();
        }
    }


    public void setCooldown(Player p, String type){
        FileConfiguration cfg = PlayerConfig.getConfig(p);
        long cd = 0;
        switch (type){
            case "daily":
                cd = System.currentTimeMillis() + 86400000;
                break;
            case "weekly":
                cd = System.currentTimeMillis() + 604800000;
                break;
            case "monthly":
                cd = System.currentTimeMillis() + Long.parseLong("2592000000");
                break;
        }
        cfg.set("rewards." + type, cd);
        PlayerConfig.getConfig(p).save();
    }
}
