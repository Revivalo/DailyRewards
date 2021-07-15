package cz.revivalo.dailyrewards.commands;

import com.tchristofferson.configupdater.ConfigUpdater;
import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.guimanager.GuiManager;
import cz.revivalo.dailyrewards.lang.Lang;
import cz.revivalo.dailyrewards.rewardmanager.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

public class RewardCommand implements CommandExecutor {
    private final DailyRewards plugin;
    private final RewardManager rewardManager;
    private final GuiManager guiManager;
    public RewardCommand(DailyRewards plugin) {
        this.plugin = plugin;
        rewardManager = plugin.getRewardManager();
        guiManager = plugin.getGuiManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("[DailyRewards] Only in-game command!");
            return true;
        } else {
            Player p = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("reward")){
                switch (args.length){
                    case 0:
                        rewardManager.claim(p, "daily", true);
                        break;
                    case 1:
                        switch (args[0]){
                            case "weekly":
                                rewardManager.claim(p, "weekly", true);
                                break;
                            case "monthly":
                                rewardManager.claim(p, "monthly", true);
                                break;
                            case "reload":
                                if (!p.hasPermission("dailyreward.manage")){
                                    p.sendMessage(Lang.PERMISSIONMSG.content(p));
                                } else {
                                    this.plugin.reloadConfig();
                                    File configFile = new File(Bukkit.getServer().getPluginManager().getPlugin("DailyRewards").getDataFolder(), "config.yml");

                                    try {
                                        ConfigUpdater.update(this.plugin, "config.yml", configFile, Collections.emptyList());
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }

                                    Lang.reload();
                                    p.sendMessage(Lang.RELOADMSG.content(p));
                                }
                                break;
                        }
                        break;
                    case 3:
                        switch (args[0]){
                            case "reset":
                                if (!p.hasPermission("dailyreward.manage")){
                                    p.sendMessage(Lang.PERMISSIONMSG.content(p));
                                    break;
                                }
                                if (rewardManager.reset(Bukkit.getOfflinePlayer(args[1]), args[2])){
                                    p.sendMessage(Lang.REWARDRESET.content(p).replace("%type%", args[2]).replace("%player%", args[1]));
                                } else {
                                    p.sendMessage(Lang.UNAVAILABLEPLAYER.content(p).replace("%player%", args[1]));
                                }
                                break;
                        }
                }
            } else if (cmd.getName().equalsIgnoreCase("rewards")){
                p.openInventory(guiManager.openRewardsMenu(p));
            }
        }
        return true;
    }
}
