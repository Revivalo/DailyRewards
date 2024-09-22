package dev.revivalo.dailyrewards.hook.papiresolver;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.file.Config;
import dev.revivalo.dailyrewards.configuration.file.Lang;
import dev.revivalo.dailyrewards.manager.cooldown.Cooldown;
import dev.revivalo.dailyrewards.manager.reward.Reward;
import dev.revivalo.dailyrewards.manager.reward.RewardType;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.user.UserHandler;
import dev.revivalo.dailyrewards.util.TextUtil;
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