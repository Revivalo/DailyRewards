package dev.revivalo.dailyrewards.utils;

import com.google.common.base.Splitter;
import dev.revivalo.dailyrewards.hooks.Hooks;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextUtils {
    private static Method COLOR_FROM_CHAT_COLOR;
    private static Method CHAT_COLOR_FROM_COLOR;
    private static final boolean hexSupport;
    private static final Pattern gradient = Pattern.compile("<(#[A-Za-z0-9]{6})>(.*?)</(#[A-Za-z0-9]{6})>");
    private static final Pattern legacyGradient = Pattern.compile("<(&[A-Za-z0-9])>(.*?)</(&[A-Za-z0-9])>");
    private static final Pattern rgb = Pattern.compile("&\\{(#......)}");

    static {
        try {
            COLOR_FROM_CHAT_COLOR = ChatColor.class.getDeclaredMethod("getColor");
            CHAT_COLOR_FROM_COLOR = ChatColor.class.getDeclaredMethod("of", Color.class);
        } catch (NoSuchMethodException e) {
            COLOR_FROM_CHAT_COLOR = null;
            CHAT_COLOR_FROM_COLOR = null;
        }
        hexSupport = CHAT_COLOR_FROM_COLOR != null;
    }

    public static String colorize(String text) {
        return colorize(text, '&');
    }

    public static String colorize(String text, char colorSymbol) {
        if (text == null) return "Not found";
        Matcher g = gradient.matcher(text);
        Matcher l = legacyGradient.matcher(text);
        Matcher r = rgb.matcher(text);
        while (g.find()) {
            Color start = Color.decode(g.group(1));
            String between = g.group(2);
            Color end = Color.decode(g.group(3));
            if (hexSupport) text = text.replace(g.group(0), rgbGradient(between, start, end, colorSymbol));
            else text = text.replace(g.group(0), between);
        }
        while (l.find()) {
            char first = l.group(1).charAt(1);
            String between = l.group(2);
            char second = l.group(3).charAt(1);
            ChatColor firstColor = ChatColor.getByChar(first);
            ChatColor secondColor = ChatColor.getByChar(second);
            if (firstColor == null) firstColor = ChatColor.WHITE;
            if (secondColor == null) secondColor = ChatColor.WHITE;
            if (hexSupport) text = text.replace(l.group(0), rgbGradient(between, fromChatColor(firstColor), fromChatColor(secondColor), colorSymbol));
            else text = text.replace(l.group(0), between);
        }
        while (r.find()) {
            if (hexSupport) {
                ChatColor color = fromColor(Color.decode(r.group(1)));
                text = text.replace(r.group(0), color + "");
            } else {
                text = text.replace(r.group(0), "");
            }
        }
        return ChatColor.translateAlternateColorCodes(colorSymbol, text);
    }

    public static String removeColors(String text) {
        return ChatColor.stripColor(text);
    }

    public static List<Character> charactersWithoutColors(String text) {
        text = removeColors(text);
        final List<Character> result = new ArrayList<>();
        for (char var : text.toCharArray()) {
            result.add(var);
        }
        return result;
    }

    public static List<String> charactersWithColors(String text) {
        return charactersWithColors(text, '§');
    }

    public static List<String> charactersWithColors(String text, char colorSymbol) {
        final List<String> result = new ArrayList<>();
        StringBuilder colors = new StringBuilder();
        boolean colorInput = false;
        boolean reading = false;
        for (char var : text.toCharArray()) {
            if (colorInput) {
                colors.append(var);
                colorInput = false;
            } else {
                if (var == colorSymbol) {
                    if (!reading) {
                        colors = new StringBuilder();
                    }
                    colorInput = true;
                    reading = true;
                    colors.append(var);
                } else {
                    reading = false;
                    result.add(colors.toString() + var);
                }
            }
        }
        return result;
    }

    private static String rgbGradient(String text, Color start, Color end, char colorSymbol) {
        final StringBuilder builder = new StringBuilder();
        text = ChatColor.translateAlternateColorCodes(colorSymbol, text);
        final List<String> characters = charactersWithColors(text);
        final double[] red = linear(start.getRed(), end.getRed(), characters.size());
        final double[] green = linear(start.getGreen(), end.getGreen(), characters.size());
        final double[] blue = linear(start.getBlue(), end.getBlue(), characters.size());
        if (text.length() == 1) {
            return fromColor(end) + text;
        }
        for (int i = 0; i < characters.size(); i++) {
            String currentText = characters.get(i);
            ChatColor current = fromColor(new Color((int) Math.round(red[i]), (int) Math.round(green[i]), (int) Math.round(blue[i])));
            builder.append(current).append(currentText.replace("§r", ""));
        }
        return builder.toString();
    }

    private static double[] linear(double from, double to, int max) {
        final double[] res = new double[max];
        for (int i = 0; i < max; i++) {
            res[i] = from + i * ((to - from) / (max - 1));
        }
        return res;
    }

    private static Color fromChatColor(ChatColor color) {
        try {
            return (Color) COLOR_FROM_CHAT_COLOR.invoke(color);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static ChatColor fromColor(Color color) {
        try {
            return (ChatColor) CHAT_COLOR_FROM_COLOR.invoke(null, color);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
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

    public static List<String> replaceList(List<String> lore, final Map<String, String> definitions) {
        String patternString = "%(" + String.join("|", definitions.keySet()) + ")%";
        Pattern pattern = Pattern.compile(patternString);

        return lore.stream()
                .map(line -> {
                    Matcher matcher = pattern.matcher(line);
                    StringBuffer sb = new StringBuffer();
                    while (matcher.find()) {
                        matcher.appendReplacement(sb, definitions.get(matcher.group(1)));
                    }
                    matcher.appendTail(sb);
                    return sb.toString();
                })
                .collect(Collectors.toList());
    }

    public static String applyPlaceholdersToString(Player player, String text) {
        return Hooks.isHookEnabled(Hooks.getPlaceholderApiHook()) && PlaceholderAPI.containsPlaceholders(text) ? PlaceholderAPI.setPlaceholders(player, text) : text;
    }

    public static List<String> applyPlaceholdersToList(Player player, List<String> list) {
        if (Hooks.isHookEnabled(Hooks.getPlaceholderApiHook())) {
            return PlaceholderAPI.setPlaceholders(player, list);
        } else return list;
    }

    public static void sendListToPlayer(final CommandSender player, final List<String> list) {
        list.forEach(player::sendMessage);
    }

    public static Set<String> getPlaceholders(String message, String symbol) {
        final String regex;
        if (symbol.equalsIgnoreCase("%")) regex = "%\\w+%";
        else if (symbol.equalsIgnoreCase("[")) regex = "\\[.*\\]";
        else regex = "\\{.*\\}";
        final Pattern pattern = Pattern.compile(regex);
        final Set<String> words = new HashSet<>();

        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            words.add(matcher.group());
        }

        return words;
    }
}
