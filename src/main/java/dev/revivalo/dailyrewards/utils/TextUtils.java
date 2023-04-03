package dev.revivalo.dailyrewards.utils;

import com.google.common.base.Splitter;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class TextUtils {
    private static final Pattern hexPattern = Pattern.compile("<#([A-Fa-f\\d]){6}>");
    public static String applyColor(String message){
        if (VersionUtils.isHexSupport()) {
            Matcher matcher = hexPattern.matcher(message);
            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, ChatColor.of(color.replace("<", "").replace(">", "")) + "");
                matcher = hexPattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String formatTime(String message, long remainingTime) {
        return replaceString(message, new HashMap<String, String>() {{
            put("%days%", String.valueOf(TimeUnit.MILLISECONDS.toDays(remainingTime)));
            put("%hours%", String.valueOf(TimeUnit.MILLISECONDS.toHours(remainingTime) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(remainingTime))));
            put("%minutes%", String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(remainingTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime))));
            put("%seconds%", String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(remainingTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime))));
        }});
    }

    public static String replaceString(String messageToReplace, final Map<String, String> definitions){
        final String[] keys = definitions.keySet().toArray(new String[0]);
        final String[] values = definitions.values().toArray(new String[0]);

        return org.apache.commons.lang.StringUtils.replaceEach(messageToReplace, keys, values);
    }

    public static List<String> replaceList(String listAsStringToReplace, final Map<String, String> definitions){
        final String[] keys = definitions.keySet().toArray(new String[]{});
        final String[] values = definitions.values().toArray(new String[]{});

        return Splitter.on("⎶").splitToList(StringUtils.replaceEach(listAsStringToReplace, keys, values));
    }

    public static void sendListToPlayer(final CommandSender player, final List<String> list) {
        list.forEach(player::sendMessage);
    }
}
