package pl.pomoku.survivalpomoku.commandManagerLib.argumentMatchers;

import org.bukkit.util.StringUtil;
import pl.pomoku.survivalpomoku.commandManagerLib.ArgumentMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Filters to leave all the strings that start with the argument string.
 * Example: kill, kick, looking | ki -> kick, kill
 */
public class StartingWithStringArgumentMatcher implements ArgumentMatcher {
    @Override
    public List<String> filter(List<String> tabCompletions, String argument) {
        List<String> result = new ArrayList<>();
        StringUtil.copyPartialMatches(argument, tabCompletions, result);
        return result;
    }
}
