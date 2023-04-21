package dev.revivalo.dailyrewards.user;

import dev.revivalo.dailyrewards.configuration.data.DataManager;

import java.util.HashMap;
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

    public static void loadUsers(){

    }

    public static User removeUser(final UUID uuid){
        final User user = usersHashMap.remove(uuid);
        DataManager.updateValues(uuid, user, user.getData());
        return user;
    }
}
