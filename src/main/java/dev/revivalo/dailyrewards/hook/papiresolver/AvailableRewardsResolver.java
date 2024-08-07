package dev.revivalo.dailyrewards.hook.papiresolver;

import dev.revivalo.dailyrewards.configuration.file.Lang;
import dev.revivalo.dailyrewards.user.UserHandler;
import org.bukkit.entity.Player;

import java.util.Optional;

public class AvailableRewardsResolver implements PlaceholderResolver {
    @Override
    public boolean canResolve(String rawPlaceholder) {
        return rawPlaceholder.startsWith("available");
    }

    @Override
    public String resolve(Player player, String rawPlaceholder) {
        return Optional.ofNullable(UserHandler.getUser(player.getUniqueId())).map(value -> String.valueOf(value.getAvailableRewards().size())).orElse(Lang.LOADING.asColoredString(player));
    }
}
