package dev.revivalo.dailyrewards.manager.reward.task;

import dev.revivalo.dailyrewards.configuration.file.Config;
import dev.revivalo.dailyrewards.configuration.file.Lang;
import dev.revivalo.dailyrewards.user.User;
import dev.revivalo.dailyrewards.util.PlayerUtil;
import dev.revivalo.dailyrewards.util.VersionUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JoinNotificationTask implements Task {
    private final Map<User, Long> playerRewardCheckTimes = new ConcurrentHashMap<>();
    private final Map<UUID, User> usersHashMap;

    public JoinNotificationTask(Map<UUID, User> usersHashMap) {
        this.usersHashMap = usersHashMap;
    }

    public void addUser(User user) {
        long checkTime = System.currentTimeMillis() + (Config.JOIN_NOTIFICATION_DELAY.asInt() * 1000L);

        playerRewardCheckTimes.put(user, checkTime);
    }

    @Override
    public BukkitRunnable get() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();

                for (User user : usersHashMap.values()) {
                    if (!playerRewardCheckTimes.containsKey(user)) continue;

                    long checkTime = playerRewardCheckTimes.get(user);
                    if (currentTime >= checkTime) {
                        Player player = user.getPlayer();

                        PlayerUtil.playSound(player, Config.JOIN_NOTIFICATION_SOUND.asString());
                        for (String line : Lang.JOIN_NOTIFICATION.asReplacedList(new HashMap<String, String>() {{
                            put("%player%", player.getName());
                            put("%rewards%", String.valueOf(user.getAvailableRewards().size()));
                        }})) {
                            BaseComponent[] msg = TextComponent.fromLegacyText(line);

                            for (BaseComponent bc : msg) {
                                if (!VersionUtil.isLegacyVersion()) bc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Lang.JOIN_HOVER_MESSAGE.asColoredString(player))));
                                bc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Config.JOIN_NOTIFICATION_COMMAND.asString().replace("%player%", player.getName())));
                            }

                            player.spigot().sendMessage(msg);
                        }

                        playerRewardCheckTimes.remove(user);
                    }
                }
            }
        };
    }

    public Map<User, Long> getPlayerRewardCheckTimes() {
        return playerRewardCheckTimes;
    }
}