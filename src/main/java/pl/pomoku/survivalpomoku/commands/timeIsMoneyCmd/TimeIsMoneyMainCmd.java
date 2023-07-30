package pl.pomoku.survivalpomoku.commands.timeIsMoneyCmd;

import pl.pomoku.survivalpomoku.commandManagerLib.MainCommand;
import pl.pomoku.survivalpomoku.commandManagerLib.argumentMatchers.ContainingAllCharsOfStringArgumentMatcher;
import pl.pomoku.survivalpomoku.commands.timeIsMoneyCmd.subCmd.ResetTimeSub;

public class TimeIsMoneyMainCmd extends MainCommand {
    public TimeIsMoneyMainCmd() {
        super(
                "<red>Nie masz uprawnień do wykonania tej komendy.",
                new ContainingAllCharsOfStringArgumentMatcher()
        );
    }

    @Override
    protected void registerSubCommands() {
        subCommands.add(new ResetTimeSub());
    }
}
