package pl.pomoku.survivalpomoku.commands.moneyCmd;

import pl.pomoku.survivalpomoku.commandManagerLib.MainCommand;
import pl.pomoku.survivalpomoku.commandManagerLib.argumentMatchers.ContainingAllCharsOfStringArgumentMatcher;
import pl.pomoku.survivalpomoku.commands.moneyCmd.subCommands.*;

public class MoneyMainCmd extends MainCommand {
    public MoneyMainCmd() {
        super(
                "<red>Nie masz uprawnień do wykonania tej komendy.",
                new ContainingAllCharsOfStringArgumentMatcher()
        );
    }

    @Override
    protected void registerSubCommands() {
        subCommands.add(new ClearAccountSub());
        subCommands.add(new MoneyHelpSub());
        subCommands.add(new MoneySingleCmd());
        subCommands.add(new AddMoneySub());
        subCommands.add(new TakeMoneySub());
    }
}
