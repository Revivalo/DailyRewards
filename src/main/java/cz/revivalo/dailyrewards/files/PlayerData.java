package cz.revivalo.dailyrewards.files;

import cz.revivalo.dailyrewards.DailyRewards;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class PlayerData extends YamlConfiguration {
    private static final Map<UUID, PlayerData> configs = new HashMap<>();

    public static PlayerData getConfig(Player player) {
        return getConfig(player.getUniqueId());
    }

    public static PlayerData getConfig(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        /*if (!new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("DailyRewards")).getDataFolder(), "userdata" + File.separator + uuid.toString() + ".yml").exists()) {
            return null;
        }*/
        return getConfig(uuid);
    }

    public static PlayerData getConfig(UUID uuid) {
        synchronized (configs) {
            if (configs.containsKey(uuid)) {
                return configs.get(uuid);
            }
            PlayerData config = new PlayerData(uuid);
            configs.put(uuid, config);
            return config;
        }
    }

    public static void removeConfigs() {
        Collection<PlayerData> oldConfs = new ArrayList<>(configs.values());
        synchronized (configs) {
            for (PlayerData config : oldConfs) {
                config.discard();
            }
        }
    }

    private File file = null;
    private final Object saveLock = new Object();
    private final UUID uuid;

    public PlayerData(UUID uuid) {
        super();
        file = new File(DailyRewards.getPlugin(DailyRewards.class).getDataFolder(), "userdata" + File.separator + uuid.toString() + ".yml");
        this.uuid = uuid;
        reload();
    }

    @SuppressWarnings("unused")
    private PlayerData() {
        uuid = null;
    }

    private void reload() {
        synchronized (saveLock) {
            try {
                load(file);
            } catch (Exception ignore) {
            }
        }
    }

    public void save() {
        synchronized (saveLock) {
            try {
                save(file);
            } catch (Exception ignore) {
            }
        }
    }

    public void discard() {
        discard(false);
    }

    public void discard(boolean save) {
        if (save) {
            save();
        }
        synchronized (configs) {
            configs.remove(uuid);
        }
    }
}
