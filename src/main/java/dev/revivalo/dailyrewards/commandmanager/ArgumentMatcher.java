package dev.revivalo.dailyrewards.commandmanager;

import java.util.List;

public interface ArgumentMatcher {
    List<String> filter(List<String> tabCompletions, String argument);
}
