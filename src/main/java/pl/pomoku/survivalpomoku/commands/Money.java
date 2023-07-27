package pl.pomoku.survivalpomoku.commands;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.pomoku.pomokupluginsrepository.commands.CommandInfo;
import pl.pomoku.pomokupluginsrepository.commands.EasyCommand;
import pl.pomoku.survivalpomoku.Account;

import java.sql.SQLException;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

@CommandInfo(name = "money", requiresPlayer = true)
@SuppressWarnings("unused")
public class Money extends EasyCommand {
    @SneakyThrows
    @Override
    public void execute(Player p, String[] args) {
        if (args.length == 0) {
            showPlayerBalance(p);
        } else if (args.length == 2 && p.hasPermission("money.admin")) {
            handleAdminCommand(args, p);
        } else if (args.length == 3 && p.hasPermission("money.admin")) {
            handleAdminCommand(args, p);
        }
    }

    private void showPlayerBalance(Player p) throws SQLException {
        Account account = plugin.getAccountDAO().getByPlayer(p);
        String color = account.getMoney() > 0 ? "<green>" : "<red>";
        p.sendMessage(strToComp("<gray>Masz na koncie: " + color + account.getMoney() + "$"));
    }

    private void handleAdminCommand(String[] args, Player p) throws SQLException {
        if (args[0].equals("clear") && args.length == 2) {
            handleClearCommand(args[1], p);
        } else if (args[0].equals("take") && args.length == 3) {
            handleTakeCommand(args[1], Double.parseDouble(args[2]), p);
        } else if (args[0].equals("add") && args.length == 3) {
            handleAddCommand(args[1], Double.parseDouble(args[2]), p);
        }
    }

    private void handleClearCommand(String playerName, Player p) throws SQLException {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null || !player.isOnline()) {
            p.sendMessage(strToComp("<red>Nie ma takiego gracza na serwerze."));
            return;
        }

        Account account = plugin.getAccountDAO().getByPlayer(player);
        if (account == null) {
            p.sendMessage(strToComp("<red>Nie ma takie gracza w systemie."));
            return;
        }

        account.setMoney(0.0);
        plugin.getAccountDAO().update(account);
        p.sendMessage(strToComp("<gray>Zresetowano stan konta graczu: <aqua>" + playerName));
    }

    private void handleTakeCommand(String playerName, double amount, Player p) throws SQLException {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null || !player.isOnline()) {
            p.sendMessage(strToComp("<red>Nie ma takiego gracza na serwerze."));
            return;
        }

        Account account = plugin.getAccountDAO().getByPlayer(player);
        if (account == null) {
            p.sendMessage(strToComp("<red>Nie ma takie gracza w systemie."));
            return;
        }

        account.setMoney(account.getMoney() - amount);
        plugin.getAccountDAO().update(account);
        p.sendMessage(strToComp("<gray>Zabrano <red>-" + amount + "$</red> graczu: <aqua>" + playerName + "</aqua> z stanu konta"));
    }

    private void handleAddCommand(String playerName, double amount, Player p) throws SQLException {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null || !player.isOnline()) {
            p.sendMessage(strToComp("<red>Nie ma takiego gracza na serwerze."));
            return;
        }

        Account account = plugin.getAccountDAO().getByPlayer(player);
        if (account == null) {
            p.sendMessage(strToComp("<red>Nie ma takie gracza w systemie."));
            return;
        }

        account.setMoney(account.getMoney() + amount);
        plugin.getAccountDAO().update(account);
        p.sendMessage(strToComp("<gray>Dodano <green>+" + amount + "$</green> graczu: <aqua>" + playerName + "</aqua> do stanu konta"));
    }
}
