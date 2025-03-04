package eu.athelion.dailyrewards.commandmanager.argumentmatcher;

import eu.athelion.dailyrewards.commandmanager.ArgumentMatcher;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class StartingWithStringArgumentMatcher implements ArgumentMatcher {
    @Override
    public List<String> filter (List<String> tabCompletions, String argument) {
        List<String> result = new ArrayList<>();
        StringUtil.copyPartialMatches(argument, tabCompletions, result);
        return result;
    }
}
