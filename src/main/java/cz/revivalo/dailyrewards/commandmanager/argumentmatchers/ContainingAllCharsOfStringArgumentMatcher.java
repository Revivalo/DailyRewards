package cz.revivalo.dailyrewards.commandmanager.argumentmatchers;

import cz.revivalo.dailyrewards.commandmanager.ArgumentMatcher;

import java.util.ArrayList;
import java.util.List;

public class ContainingAllCharsOfStringArgumentMatcher implements ArgumentMatcher {
    @Override
    public List<String> filter(List<String> tabCompletions, String argument) {
        List<String> result = new ArrayList<>();

        for (String tabCompletion : tabCompletions) {
            boolean passes = true;

            for (char c : argument.toCharArray()) {
                passes = tabCompletion.contains(String.valueOf(c));

                if (!passes)
                    break;
            }

            if (passes)
                result.add(tabCompletion);
        }

        return result;
    }
}
