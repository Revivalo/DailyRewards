package cz.revivalo.dailyrewards.commandmanager;

import java.util.List;

public interface ArgumentMatcher {
    public List<String> filter(List<String> tabCompletions, String argument);
}
