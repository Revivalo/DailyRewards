package dev.revivalo.dailyrewards.utils;

import com.google.common.base.Splitter;
import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.hooks.Hooks;
import dev.revivalo.dailyrewards.managers.reward.ActionType;
import dev.revivalo.dailyrewards.managers.reward.RewardAction;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextUtils {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<(#[A-Fa-f0-9]{6})>(.*?)</(#[A-Fa-f0-9]{6})>");
    private static final Pattern LEGACY_GRADIENT_PATTERN = Pattern.compile("<(&[A-Za-z0-9])>(.*?)</(&[A-Za-z0-9])>");
    private static final Pattern RGB_PATTERN = Pattern.compile("<(#......)>");

    public static List<String> colorize(List<String> list) {
        final List<String> coloredList = new ArrayList<>();
        for (String line : list) {
            coloredList.add(colorize(line));
        }
        return coloredList;
    }

    public static String colorize(String text) {
        if (text == null)
            return "Not found";

        if (VersionUtils.isHexSupport()) {
            text = processGradientColors(text);
            text = processLegacyGradientColors(text, LEGACY_GRADIENT_PATTERN);
            text = processRGBColors(text);
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private static String processGradientColors(String text) {
        Matcher matcher = TextUtils.GRADIENT_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            Color startColor = Color.decode(matcher.group(1));
            String between = matcher.group(2);
            Color endColor = Color.decode(matcher.group(3));
            BeforeType[] types = BeforeType.detect(between);
            between = BeforeType.replaceColors(between);
            String gradient = rgbGradient(between, startColor, endColor, types);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(gradient));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String processLegacyGradientColors(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            char first = matcher.group(1).charAt(1);
            String between = matcher.group(2);
            char second = matcher.group(3).charAt(1);
            ChatColor firstColor = ChatColor.getByChar(first);
            ChatColor secondColor = ChatColor.getByChar(second);
            BeforeType[] types = BeforeType.detect(between);
            between = BeforeType.replaceColors(between);
            if (firstColor == null) {
                firstColor = ChatColor.WHITE;
            }
            if (secondColor == null) {
                secondColor = ChatColor.WHITE;
            }
            String gradient = rgbGradient(between, firstColor.getColor(), secondColor.getColor(), types);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(gradient));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String processRGBColors(String text) {
        Matcher matcher = TextUtils.RGB_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            ChatColor color = ChatColor.of(Color.decode(matcher.group(1)));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(color.toString()));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String rgbGradient(String str, Color from, Color to, BeforeType[] types) {
        final double[] red = linear(from.getRed(), to.getRed(), str.length());
        final double[] green = linear(from.getGreen(), to.getGreen(), str.length());
        final double[] blue = linear(from.getBlue(), to.getBlue(), str.length());
        StringBuilder before = new StringBuilder();
        for (BeforeType type : types) {
            before.append(ChatColor.getByChar(type.getCode()));
        }
        final StringBuilder builder = new StringBuilder();
        if (str.length() == 1) {
            return ChatColor.of(to) + before.toString() + str;
        }
        for (int i = 0; i < str.length(); i++) {
            builder.append(ChatColor.of(new Color((int) Math.round(red[i]), (int) Math.round(green[i]), (int) Math.round(blue[i])))).append(before).append(str.charAt(i));
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

    public enum BeforeType {
        MIXED('k'),
        BOLD('l'),
        CROSSED('m'),
        UNDERLINED('n'),
        CURSIVE('o');

        private final char code;

        BeforeType(char code) {
            this.code = code;
        }

        public char getCode() {
            return code;
        }

        public static BeforeType[] detect(String text) {
            List<BeforeType> values = new ArrayList<>();
            if (text.contains("&k")) {
                values.add(MIXED);
            }
            if (text.contains("&l")) {
                values.add(BOLD);
            }
            if (text.contains("&m")) {
                values.add(CROSSED);
            }
            if (text.contains("&n")) {
                values.add(UNDERLINED);
            }
            if (text.contains("&o")) {
                values.add(CURSIVE);
            }
            return values.toArray(new BeforeType[0]);
        }

        public static String replaceColors(String text) {
            return text.replaceAll("&[kmno]", "");
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

    public static List<RewardAction> findAndReturnActions(List<String> actions) {
        final List<RewardAction> actionList = new ArrayList<>();
        for (String line : actions) {
            String action;
            String statement;

            if (line.startsWith("[")) {
                int startIndex = line.indexOf("[") + 1;
                int endIndex = line.indexOf("]");
                action = line.substring(startIndex, endIndex);
                statement = line.replace("[" + action + "]", "").trim();
            } else {
                action = "CONSOLE";
                statement = line.trim();
            }

            try {
                actionList.add(RewardAction.builder()
                        .setActionType(ActionType.valueOf(action.toUpperCase(Locale.ENGLISH)))
                        .setExecutedCommand(statement)
                        .build());
            } catch (IllegalArgumentException ex) {
                DailyRewardsPlugin.get().getLogger().info(action + " is invalid reward action!");
            }
        }
        return actionList;
    }

    public static Set<String> getPlaceholders(String message, String symbol) {
        final String regex;
        if (symbol.equalsIgnoreCase("%")) regex = "%\\w+%";
        else if (symbol.equalsIgnoreCase("[")) regex = "\\[.*]";
        else regex = "\\{.*}";
        final Pattern pattern = Pattern.compile(regex);
        final Set<String> words = new HashSet<>();

        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            words.add(matcher.group());
        }

        return words;
    }

    public static List<String> replaceListAsString(String listAsStringToReplace, final Map<String, String> definitions) {
        return Splitter.on("ᴪ").splitToList(replaceString(listAsStringToReplace, definitions));
    }
}
