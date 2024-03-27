package dev.revivalo.dailyrewards.configuration;

import dev.revivalo.dailyrewards.utils.TextUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class PlaceholderColorTextModifier implements TextModifier {
    @Override
    public String modifyText(OfflinePlayer player, String text) {
        return PlaceholderAPI.setPlaceholders(player, TextUtils.colorize(text));
    }

    @Override
    public List<String> modifyList(OfflinePlayer player, List<String> list) {
        return PlaceholderAPI.setPlaceholders(player, TextUtils.colorize(list));
    }
}
