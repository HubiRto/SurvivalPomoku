package pl.pomoku.survivalpomoku.commands.marketCmd.subCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pomoku.survivalpomoku.commandManagerLib.SubCommand;
import pl.pomoku.survivalpomoku.menusystem.MarketMenu;

import java.util.List;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class MarketSingleCmd implements SubCommand {
    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Otwiera GUI z rynkiem.";
    }

    @Override
    public String getSyntax() {
        return "/market";
    }

    @Override
    public String getPermission() {
        return "market.gui";
    }

    @Override
    public List<String> getTabCompletion(int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage(strToComp("<red>Nie możesz wykonać tej komendy z konsoli!"));
            return;
        }
        new MarketMenu(plugin.getPlayerMenuUtility(player)).open();
    }
}
