package cz.revivalo.dailyrewards.rewardmanager;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.lang.Lang;
import cz.revivalo.dailyrewards.playerconfig.PlayerConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RewardManager {
    private final DailyRewards plugin;
    private final Cooldowns cooldowns;
    public RewardManager(DailyRewards plugin){
        this.plugin = plugin;
        cooldowns = plugin.getCooldowns();
    }
    /*
    TODO:
    MySQL support
     */

    public void claim(Player p, String type, boolean fromCommand){
        long cd = Long.parseLong(cooldowns.getCooldown(p, type, false));
        if (!p.hasPermission("dailyreward." + type)){
            if (fromCommand) {
                p.sendMessage(Lang.PERMISSIONMSG.content(p));
            }
            return;
        }
        if (cd >= 0){
            String time = cooldowns.getCooldown(p, type, true);
            if (fromCommand){
                p.sendMessage(Lang.COOLDOWNMESSAGE.content(p).replace("%type%", type).replace("%time%", time));
            } else {
                p.playSound(p.getLocation(), Sound.valueOf(Lang.UNAVAILABLEREWARDSOUND.content(p).toUpperCase()), 1F, 1F);
            }
        } else {
            List<String> rewards = Lang.valueOf(type.toUpperCase() + plugin.getPremium(p, type) + "REWARDS").contentLore(p);
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            if (rewards.size() != 0) {
                for (String str : rewards) {
                    Bukkit.dispatchCommand(console, str.replace("%random%", String.valueOf(new Random().nextInt(200) + 100)).replace("%player%", p.getName()));
                }
            } else {
                p.sendMessage(Lang.REWARDDONTSET.content(p));
            }
            p.playSound(p.getLocation(), Sound.valueOf(Lang.valueOf(type.toUpperCase() + "SOUND").content(p).toUpperCase()), 1F, 1F);
            p.sendTitle(Lang.valueOf(type.toUpperCase() + "TITLE").content(p), Lang.valueOf(type.toUpperCase() + "SUBTITLE").content(p), 15, 35, 15);
            cooldowns.setCooldown(p, type);
            if (Boolean.parseBoolean(Lang.ANNOUNCEENABLED.content(p))) {
                announce(Lang.valueOf(type.toUpperCase() + plugin.getPremium(p, type) + "COLLECTED").content(p).replace("%player%", p.getName()));
            }
            if (!fromCommand) {
                p.closeInventory();
            }
        }
    }

    public boolean reset(OfflinePlayer p, String type){
        if (p.isOnline() || p.hasPlayedBefore()) {
            FileConfiguration data = PlayerConfig.getConfig(p);
            if (type.equalsIgnoreCase("all")) {
                Objects.requireNonNull(data).set("rewards", null);
            } else {
                Objects.requireNonNull(data).set("rewards." + type, 0);
            }
            Objects.requireNonNull(PlayerConfig.getConfig(p)).save();
            return true;
        } else {
            return false;
        }
    }

    private void announce(String msg){
        for (Player p : Bukkit.getOnlinePlayers()){
            p.sendMessage(msg);
        }
    }
}
