package pl.pomoku.survivalpomoku.commands.moneyCmd.subCommands;

import lombok.SneakyThrows;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pomoku.survivalpomoku.Account;
import pl.pomoku.survivalpomoku.commandManagerLib.SubCommand;

import java.sql.SQLException;
import java.util.List;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class MoneySingleCmd implements SubCommand {
    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Zwraca stan konta gracza";
    }

    @Override
    public String getSyntax() {
        return "/money";
    }

    @Override
    public String getPermission() {
        return "money.balance";
    }

    @Override
    public List<String> getTabCompletion(int index, String[] args) {
        return null;
    }

    @Override
    @SneakyThrows
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(strToComp("<red>Nie możesz wykonać tej komendy z konsoli!"));
            return;
        }

        Account account = plugin.getAccountDAO().getByPlayer(player);
        String textColor = account.getMoney() >= 0 ? "<green>" : "<red>";
        player.sendMessage(strToComp("<gray>Twój stan konta: " + textColor + account.getMoney() + "<green>$"));
    }
}
