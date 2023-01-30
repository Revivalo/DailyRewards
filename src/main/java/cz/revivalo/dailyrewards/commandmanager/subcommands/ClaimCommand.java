package cz.revivalo.dailyrewards.commandmanager.subcommands;

import cz.revivalo.dailyrewards.DailyRewards;
import cz.revivalo.dailyrewards.commandmanager.SubCommand;
import cz.revivalo.dailyrewards.configuration.enums.Lang;
import cz.revivalo.dailyrewards.managers.reward.RewardType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClaimCommand implements SubCommand {
    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getDescription() {
        return "Claims a stated reward";
    }

    @Override
    public String getSyntax() {
        return "/reward claim <rewardType>";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return Arrays.stream(RewardType.values()).map(reward -> reward.toString()).collect(Collectors.toList());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        String rewardType;
        try {
            rewardType = args[0];
        } catch (Exception exception){
            sender.sendMessage(Lang.VALID_COMMAND_USAGE.asColoredString().replace("%usage%", getSyntax()));
            return;
        }
        if (!(sender instanceof Player)){
            sender.sendMessage("[DailyRewards] Command is only executable in-game!");
            return;
        }
        DailyRewards.getRewardManager().claim((Player) sender, RewardType.findByName(rewardType), true, true);
    }
}
