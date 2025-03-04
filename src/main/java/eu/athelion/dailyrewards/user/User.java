package eu.athelion.dailyrewards.user;

import eu.athelion.dailyrewards.DailyRewardsPlugin;
import eu.athelion.dailyrewards.data.DataManager;
import eu.athelion.dailyrewards.manager.Setting;
import eu.athelion.dailyrewards.manager.cooldown.Cooldown;
import eu.athelion.dailyrewards.manager.reward.RewardType;
import eu.athelion.dailyrewards.util.PermissionUtil;
import eu.athelion.dailyrewards.util.TextUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class User {
    private final Player player;
    @Setter
    private Map<String, Object> data;

    public User(Player player, Map<String, Object> data) {
        this.player = player;
        this.data = data;
    }

    public Cooldown getCooldown(RewardType rewardType) {
        return new Cooldown(Long.parseLong(String.valueOf(data.get(rewardType.toString()))));
    }

    public Set<RewardType> getAvailableRewards(){
        Set<RewardType> availableRewards = new HashSet<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            final RewardType rewardType = RewardType.findByName(entry.getKey());
            if (rewardType == null) continue;
            if (!rewardType.isEnabled()) continue;
            if (new Cooldown(Long.parseLong(String.valueOf(entry.getValue()))).isClaimable()) availableRewards.add(RewardType.findByName(entry.getKey()));
        }
        return availableRewards;
    }

    public boolean isAdmin() {
        return player.isOp() || PermissionUtil.hasPermission(player, PermissionUtil.Permission.ADMIN_PERMISSION);
    }

    public boolean hasPermission(PermissionUtil.Permission permission) {
        return PermissionUtil.hasPermission(player, permission);
    }

    public void sendMessage(String message) {
        player.sendMessage(TextUtil.colorize(message));
    }

    public boolean isOnline() {
        return player.isOnline();
    }

    /**
     *
     * @return True if the setting was enabled
     */
    public boolean toggleSetting(Setting setting, boolean set) {
        data.put(setting.getTag(), set ? "1" : "0");

        DailyRewardsPlugin.get().runAsync(() -> {
            DataManager.updateValues(
                    player.getUniqueId(),
                    this,
                    new HashMap<String, Object>() {{
                        put(setting.getTag(), hasSettingEnabled(setting) ? 1L : 0);
                    }}
            );
        });

        return set;
    }

    public boolean hasSettingEnabled(Setting setting) {
        return 1 == Long.parseLong(String.valueOf(data.get(setting.getTag())));
    }

    public void updateData(Map<String, Object> changes) {
        data.putAll(changes);
    }
}
