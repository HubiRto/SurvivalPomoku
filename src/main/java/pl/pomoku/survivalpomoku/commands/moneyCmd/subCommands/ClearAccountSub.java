package pl.pomoku.survivalpomoku.commands.moneyCmd.subCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pomoku.survivalpomoku.Account;
import pl.pomoku.survivalpomoku.commandManagerLib.SubCommand;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class ClearAccountSub implements SubCommand {
    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Czyści stan konta gracz na 0.0";
    }

    @Override
    public String getSyntax() {
        return "/money clear <player>";
    }

    @Override
    public String getPermission() {
        return "money.clear";
    }

    @Override
    public List<String> getTabCompletion(int index, String[] args) {
        return switch (index) {
            case 0 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            default -> null;
        };
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        String playerName;

        try {
            playerName = args[0];
        } catch (Exception exception) {
            sender.sendMessage(strToComp("<red>Użycie: <gray>" + getSyntax()));
            return;
        }

        Player player = Bukkit.getPlayer(playerName);

        if (player == null || !player.isOnline()) {
            sender.sendMessage(strToComp("<red>Nieprawidłowy gracz."));
            return;
        }

        Account account;

        try {
            account = plugin.getAccountDAO().getByPlayer(player);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(account == null){
            sender.sendMessage(strToComp("<red>Nie ma takiego gracza w bazie."));
            return;
        }

        account.setMoney(0.0);

        try {
            plugin.getAccountDAO().update(account);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        sender.sendMessage(strToComp("<gray>Zresetowano stan konta graczu: <aqua>" + playerName));
    }
}
