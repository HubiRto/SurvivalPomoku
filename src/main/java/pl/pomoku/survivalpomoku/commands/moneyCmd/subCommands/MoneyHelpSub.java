package pl.pomoku.survivalpomoku.commands.moneyCmd.subCommands;

import org.bukkit.command.CommandSender;
import pl.pomoku.survivalpomoku.commandManagerLib.SubCommand;

import java.util.List;

import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class MoneyHelpSub implements SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Pomoc dla komendy MONEY";
    }

    @Override
    public String getSyntax() {
        return "/money help";
    }

    @Override
    public String getPermission() {
        return "money.help";
    }

    @Override
    public List<String> getTabCompletion(int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        for (SubCommand subCommand : plugin.getMoneyCmd().getSubCommands())
        {
            if (sender.hasPermission(subCommand.getPermission()))
                sender.sendMessage(subCommand.getSyntax() + " - " + subCommand.getDescription() + " (" + subCommand.getPermission() + ")");
        }
    }
}
