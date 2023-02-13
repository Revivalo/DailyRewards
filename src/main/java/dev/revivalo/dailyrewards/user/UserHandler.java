package dev.revivalo.dailyrewards.user;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class UserHandler {
    private static final HashMap<UUID, User> usersHashMap = new HashMap<>();
    public static User addUser(final User user){
        usersHashMap.put(user.getPlayer().getUniqueId(), user);
        return user;
    }

    public static void setUsersAvailableRewards(final UUID uuid, short availableRewards){
        Optional<User> userOptional = Optional.ofNullable(usersHashMap.getOrDefault(uuid, null));
        userOptional.ifPresent(user -> user.setAvailableRewards(availableRewards));
    }

    public static Optional<User> getUser(final UUID uuid){
        return Optional.of(usersHashMap.get(uuid));
    }

    public static User removeUser(final UUID uuid){
        return usersHashMap.remove(uuid);
    }
}
