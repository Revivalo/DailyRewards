package dev.revivalo.dailyrewards.configuration;

import dev.revivalo.dailyrewards.utils.TextUtils;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class ColorTextModifier implements TextModifier {
    @Override
    public String modifyText(OfflinePlayer player, String text) {
        return TextUtils.colorize(text);
    }

    @Override
    public List<String> modifyList(OfflinePlayer player, List<String> list) {
        return TextUtils.colorize(list);
    }

}
