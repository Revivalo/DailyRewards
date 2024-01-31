package dev.revivalo.dailyrewards.user;

import dev.revivalo.dailyrewards.configuration.data.DataManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class UserHandler {
    private static final HashMap<UUID, User> usersHashMap = new HashMap<>();
    public static User addUser(final User user){
        usersHashMap.put(user.getPlayer().getUniqueId(), user);
        return user;
    }

    public static User getUser(final UUID uuid){
        return usersHashMap.get(uuid);
    }

    @Nullable
    public static User getUser(@Nullable Player player) {
        return Optional.ofNullable(player)
                .map(Player::getUniqueId)
                .map(UserHandler::getUser)
                .orElse(null);
    }

    public static void removeUser(final UUID uuid){
        final User user = usersHashMap.remove(uuid);

        if (user != null)
            DataManager.updateValues(uuid, user, user.getData());
    }
}
