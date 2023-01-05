package cz.revivalo.dailyrewards.managers;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.RewardType;
import cz.revivalo.dailyrewards.files.Config;
import cz.revivalo.dailyrewards.files.DataManager;
import cz.revivalo.dailyrewards.files.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Locale;

public class RewardManager {
    private final DailyRewards plugin;
    public RewardManager(final DailyRewards plugin){
        this.plugin = plugin;
    }

    public void autoClaim(final Player player, Collection<RewardType> rewardTypes){
        if (Config.CHECK_FOR_FULL_INVENTORY.asBoolean() && player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Lang.FULL_INVENTORY_MESSAGE.asColoredString());
            return;
        }
        final StringBuilder stringBuilder = new StringBuilder();
        for (final RewardType rewardType : rewardTypes){
            stringBuilder.append(getRewardPlaceholder(rewardType)).append(", ");
            claim(player, rewardType, false, false);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        Lang.sendListToPlayer(player, Lang.AUTO_CLAIMED_NOTIFICATION.asColoredList("%rewards%", stringBuilder.toString()));//player.sendMessage(Lang.AUTO_CLAIMED_NOTIFICATION.asColoredList("%rewards%", stringBuilder.toString()));
    }

    public void claim(final Player player, RewardType type, boolean fromCommand, boolean announce){
        final Cooldown cooldown = Cooldowns.getCooldown(player, type);
        if (!player.hasPermission("dailyreward." + type)){
            if (fromCommand)
                player.sendMessage(Lang.PERMISSION_MESSAGE.asColoredString());

            return;
        }
        if (Config.CHECK_FOR_FULL_INVENTORY.asBoolean() && player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Lang.FULL_INVENTORY_MESSAGE.asColoredString());
            return;
        }
        if (cooldown.isClaimable()) {
            final Collection<String> rewardCommands = Config.valueOf(type.toStringInUppercase() + plugin.getPremium(player, type) + "_REWARDS").asReplacedString("%player%", player.getName());
            final ConsoleCommandSender console = Bukkit.getConsoleSender();
            if (rewardCommands.size() != 0) {
                for (final String command : rewardCommands) {
                    Bukkit.dispatchCommand(console, command);
                }
            } else {
                player.sendMessage(Lang.REWARDS_IS_NOT_SET.asColoredString());
            }

            //Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title  title [{'text':'' + Lang.valueOf(type.toUpperCase(Locale.ENGLISH) + "TITLE") + '","color":"gold"}]"'");
            //player.sendTitle(Lang.valueOf(type.toStringInUppercase() + "_TITLE").asColoredString(), Lang.valueOf(type.toStringInUppercase() + "_SUBTITLE").asColoredString());//, 15, 35, 15);
            Cooldowns.setCooldown(player, type);
            if (announce){
                player.playSound(player.getLocation(), Sound.valueOf(Config.valueOf(type.toStringInUppercase() + "_SOUND").asStringInUppercase()), 1F, 1F);
                player.sendTitle(Lang.valueOf(type.toStringInUppercase() + "_TITLE").asColoredString(), Lang.valueOf(type.toStringInUppercase() + "_SUBTITLE").asColoredString());//, 15, 35, 15);
                if (Config.ANNOUNCE_ENABLED.asBoolean())
                    announce(Lang.valueOf(type.toStringInUppercase() + plugin.getPremium(player, type) + "_COLLECTED").asPlaceholderApiReplacedString(player).replace("%player%", player.getName()));
            }
            if (!fromCommand)
                player.closeInventory();

        } else {
            if (fromCommand){
                player.sendMessage(Lang.COOLDOWN_MESSAGE.asColoredString().replace("%type%", getRewardPlaceholder(type)).replace("%time%", cooldown.getFormat()));
            } else {
                player.playSound(player.getLocation(), Sound.valueOf(Config.UNAVAILABLE_REWARD_SOUND.asString().toUpperCase(Locale.ENGLISH)), 1F, 1F);
            }
        }
    }

    private String getRewardPlaceholder(final RewardType reward) {
        switch (reward){
            case DAILY:
                return Config.DAILY_PLACEHOLDER.asString();
            case WEEKLY:
                return Config.WEEKLY_PLACEHOLDER.asString();
            default:
                return Config.MONTHLY_PLACEHOLDER.asString();
        }
    }

    public boolean reset(final OfflinePlayer player, RewardType type){
        if (player.isOnline() || player.hasPlayedBefore()) {
            if (type.toString().equalsIgnoreCase("all")) {
                DataManager.setValues(player.getUniqueId(), "daily", 0L, "weekly", 0L, "monthly", 0L);
            } else {
                DataManager.setValues(player.getUniqueId(), type.toString(), 0L);
            }
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
