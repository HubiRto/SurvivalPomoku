package pl.pomoku.survivalpomoku.commands.marketCmd;

import pl.pomoku.survivalpomoku.commandManagerLib.MainCommand;
import pl.pomoku.survivalpomoku.commandManagerLib.argumentMatchers.ContainingAllCharsOfStringArgumentMatcher;
import pl.pomoku.survivalpomoku.commands.marketCmd.subCommands.AddMarketItemSub;
import pl.pomoku.survivalpomoku.commands.marketCmd.subCommands.MarketSingleCmd;

public class MarketMainCmd extends MainCommand {
    public MarketMainCmd() {
        super(
                "<red>Nie masz uprawnień do wykonania tej komendy.",
                new ContainingAllCharsOfStringArgumentMatcher()
        );
    }

    @Override
    protected void registerSubCommands() {
        subCommands.add(new MarketSingleCmd());
        subCommands.add(new AddMarketItemSub());
    }
}
