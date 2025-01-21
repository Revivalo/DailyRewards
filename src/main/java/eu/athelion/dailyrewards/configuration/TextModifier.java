package eu.athelion.dailyrewards.configuration;

import org.bukkit.OfflinePlayer;

import java.util.List;

public interface TextModifier {
    String modifyText(OfflinePlayer player, String text);

    List<String> modifyList(OfflinePlayer player, List<String> list);
}