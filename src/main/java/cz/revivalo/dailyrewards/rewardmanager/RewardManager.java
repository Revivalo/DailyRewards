package cz.revivalo.dailyrewards.rewardmanager;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.files.Lang;
import cz.revivalo.dailyrewards.files.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RewardManager {
    private final DailyRewards plugin;
    private final Cooldowns cooldowns;
    public RewardManager(final DailyRewards plugin){
        this.plugin = plugin;
        cooldowns = plugin.getCooldowns();
    }

    public void claim(final Player player, String type, boolean fromCommand){
        final Cooldown cooldown = cooldowns.getCooldown(player, type);
        if (!player.hasPermission("dailyreward." + type)){
            if (fromCommand) {
                player.sendMessage(Lang.PERMISSION_MESSAGE.content(player));
            }
            return;
        }
        if (cooldown.isClaimable()){
            final List<String> rewards = Lang.valueOf(type.toUpperCase(Locale.ENGLISH) + plugin.getPremium(player, type) + "_REWARDS").getColoredList(player, "%player%", player.getName());
            final ConsoleCommandSender console = Bukkit.getConsoleSender();
            if (rewards.size() != 0) {
                for (String str : rewards) {
                    Bukkit.dispatchCommand(console, str);
                }
            } else {
                player.sendMessage(Lang.REWARD_DONT_SET.content(player));
            }


            player.playSound(player.getLocation(), Sound.valueOf(Lang.valueOf(type.toUpperCase(Locale.ENGLISH) + "_SOUND").content(player).toUpperCase(Locale.ENGLISH)), 1F, 1F);
            //Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title  title [{'text':'' + Lang.valueOf(type.toUpperCase(Locale.ENGLISH) + "TITLE") + '","color":"gold"}]"'");
            player.sendTitle(Lang.valueOf(type.toUpperCase(Locale.ENGLISH) + "_TITLE").content(player), Lang.valueOf(type.toUpperCase(Locale.ENGLISH) + "_SUBTITLE").content(player), 15, 35, 15);
            cooldowns.setCooldown(player, type);
            if (Lang.ANNOUNCE_ENABLED.getBoolean()) {
                announce(Lang.valueOf(type.toUpperCase(Locale.ENGLISH) + plugin.getPremium(player, type) + "_COLLECTED").content(player).replace("%player%", player.getName()));
            }
            if (!fromCommand) {
                player.closeInventory();
            }
        } else {
            if (fromCommand){
                player.sendMessage(Lang.COOLDOWN_MESSAGE.content(player).replace("%type%", getRewardPlaceholder(type)).replace("%time%", cooldown.getFormat()));
            } else {
                player.playSound(player.getLocation(), Sound.valueOf(Lang.UNAVAILABLE_REWARD_SOUND.getText().toUpperCase(Locale.ENGLISH)), 1F, 1F);
            }
        }
    }

    private String getRewardPlaceholder(final String reward) {
        switch (reward){
            case "daily":
                return Lang.DAILY_PLACEHOLDER.getText();
            case "weekly":
                return Lang.WEEKLY_PLACEHOLDER.getText();
            default:
                return Lang.MONTHLY_PLACEHOLDER.getText();
        }
    }

    public boolean reset(final OfflinePlayer player, String type){
        if (player.isOnline() || player.hasPlayedBefore()) {
            final FileConfiguration data = PlayerData.getConfig(player);
            if (type.equalsIgnoreCase("all")) {
                Objects.requireNonNull(data).set("rewards", null);
            } else {
                data.set("rewards." + type, 0);
            }
            Objects.requireNonNull(PlayerData.getConfig(player)).save();
            return true;
        } else {
            return false;
        }
    }

    private void announce(final String message){
        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()){
            onlinePlayer.sendMessage(message);
        }
    }
}
