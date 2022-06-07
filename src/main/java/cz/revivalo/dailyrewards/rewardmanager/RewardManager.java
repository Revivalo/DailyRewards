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

    public void claim(final Player player, String type, boolean fromCommand){
        long cd = Long.parseLong(cooldowns.getCooldown(player, type, false));
        if (!player.hasPermission("dailyreward." + type)){
            if (fromCommand) {
                player.sendMessage(Lang.PERMISSIONMSG.content(player));
            }
            return;
        }
        if (cd >= 0){
            String time = cooldowns.getCooldown(player, type, true);
            if (fromCommand){
                player.sendMessage(Lang.COOLDOWNMESSAGE.content(player).replace("%type%", getRewardPlaceholder(type)).replace("%time%", time));
            } else {
                player.playSound(player.getLocation(), Sound.valueOf(Lang.UNAVAILABLEREWARDSOUND.content(player).toUpperCase()), 1F, 1F);
            }
        } else {
            List<String> rewards = Lang.valueOf(type.toUpperCase() + plugin.getPremium(player, type) + "REWARDS").contentLore(player);
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            if (rewards.size() != 0) {
                for (String str : rewards) {
                    Bukkit.dispatchCommand(console, str.replace("%random%", String.valueOf(new Random().nextInt(200) + 100)).replace("%player%", player.getName()));
                }
            } else {
                player.sendMessage(Lang.REWARDDONTSET.content(player));
            }
            player.playSound(player.getLocation(), Sound.valueOf(Lang.valueOf(type.toUpperCase() + "SOUND").content(player).toUpperCase()), 1F, 1F);
            player.sendTitle(Lang.valueOf(type.toUpperCase() + "TITLE").content(player), Lang.valueOf(type.toUpperCase() + "SUBTITLE").content(player), 15, 35, 15);
            cooldowns.setCooldown(player, type);
            if (Lang.ANNOUNCEENABLED.getBoolean()) {
                announce(Lang.valueOf(type.toUpperCase() + plugin.getPremium(player, type) + "COLLECTED").content(player).replace("%player%", player.getName()));
            }
            if (!fromCommand) {
                player.closeInventory();
            }
        }
    }

    private String getRewardPlaceholder(final String reward) {
        switch (reward){
            case "daily":
                return Lang.DAILYPLACEHOLDER.content();
            case "weekly":
                return Lang.WEEKLYPLACEHOLDER.content();
            default:
                return Lang.MONTHLYPLACEHOLDER.content();
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
