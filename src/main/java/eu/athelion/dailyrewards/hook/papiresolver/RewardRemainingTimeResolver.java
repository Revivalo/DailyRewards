package eu.athelion.dailyrewards.hook.papiresolver;

import eu.athelion.dailyrewards.DailyRewardsPlugin;
import eu.athelion.dailyrewards.configuration.file.Config;
import eu.athelion.dailyrewards.configuration.file.Lang;
import eu.athelion.dailyrewards.manager.cooldown.Cooldown;
import eu.athelion.dailyrewards.manager.reward.Reward;
import eu.athelion.dailyrewards.manager.reward.RewardType;
import eu.athelion.dailyrewards.user.User;
import eu.athelion.dailyrewards.user.UserHandler;
import eu.athelion.dailyrewards.util.TextUtil;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RewardRemainingTimeResolver implements PlaceholderResolver {
    private static final Pattern pattern = Pattern.compile("cooldown_(.*)");

    @Override
    public boolean canResolve(String rawPlaceholder)  {
        return pattern.matcher(rawPlaceholder).find();
    }

    @Override
    public String resolve(Player p, String rawPlaceholder) {
        Matcher matcher = pattern.matcher(rawPlaceholder);
        if (!matcher.find())
            return null;

        RewardType rewardType = RewardType.findByName(matcher.group(1));
        Optional<Reward> rewardOptional = DailyRewardsPlugin.getRewardManager().getRewards().stream().filter(reward -> reward.getType() == rewardType).findAny();
        if (!rewardOptional.isPresent()) {
            return "Invalid type!";
        }

        User user = UserHandler.getUser(p);
        if (user != null) {
            Cooldown cooldown = user.getCooldown(rewardType);
            if (cooldown.isClaimable()) return Lang.AVAILABLE.asColoredString();
            else                        return TextUtil.formatTime(Config.valueOf(rewardType.name() + "_COOLDOWN_FORMAT").asString(), cooldown.getTimeLeftInMillis());
        }

        return Lang.LOADING.asColoredString();
    }
}