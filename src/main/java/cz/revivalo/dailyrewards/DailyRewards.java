package cz.revivalo.dailyrewards;

import com.tchristofferson.configupdater.ConfigUpdater;
import cz.revivalo.dailyrewards.commands.RewardCommand;
import cz.revivalo.dailyrewards.guimanager.ClickEvent;
import cz.revivalo.dailyrewards.guimanager.GuiManager;
import cz.revivalo.dailyrewards.playerconfig.PlayerConfig;
import cz.revivalo.dailyrewards.rewardmanager.Cooldowns;
import cz.revivalo.dailyrewards.rewardmanager.JoinNotification;
import cz.revivalo.dailyrewards.rewardmanager.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

public final class DailyRewards extends JavaPlugin {

    private DailyRewards plugin;
    private Cooldowns cooldowns;
    private RewardManager rewardManager;
    private GuiManager guiManager;
    public boolean papi = false;
    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");

        try {
            ConfigUpdater.update(plugin, "config.yml", configFile, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        reloadConfig();
        cooldowns = new Cooldowns();
        rewardManager = new RewardManager(plugin);
        guiManager = new GuiManager(plugin);
        registerCommands();
        implementsListeners();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            papi = true;
        } else {
            plugin.getLogger().warning("Could not find PlaceholderAPI, placeholders will not work");
        }
    }

    @Override
    public void onDisable() {
        plugin = null;
        PlayerConfig.removeConfigs();
    }

    void registerCommands(){
        Objects.requireNonNull(getCommand("reward")).setExecutor(new RewardCommand(plugin));
        Objects.requireNonNull(getCommand("rewards")).setExecutor(new RewardCommand(plugin));
    }

    void implementsListeners(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ClickEvent(rewardManager), plugin);
        pm.registerEvents(new JoinNotification(plugin), plugin);
    }

    public String getPremium(Player p, String type){
        if (p.hasPermission("dailyreward." + type + ".premium")){
            return "PREMIUM";
        } else {
            return "";
        }
    }

    public Cooldowns getCooldowns() {return cooldowns;}

    public RewardManager getRewardManager() {return rewardManager;}

    public GuiManager getGuiManager() {
        return guiManager;
    }
}
