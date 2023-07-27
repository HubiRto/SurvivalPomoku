package pl.pomoku.survivalpomoku.commands.moneyCmd.subCommands;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pomoku.survivalpomoku.commandManagerLib.SubCommand;
import pl.pomoku.survivalpomoku.entity.Account;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class TakeMoneySub implements SubCommand {
    @Override
    public String getName() {
        return "take";
    }

    @Override
    public String getDescription() {
        return "Zabiera pieniądze z konta gracza";
    }

    @Override
    public String getSyntax() {
        return "/money take <player> <amount>";
    }

    @Override
    public String getPermission() {
        return "money.take";
    }

    @Override
    public List<String> getTabCompletion(int index, String[] args) {
        return switch (index) {
            case 0 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            default -> null;
        };
    }

    @SneakyThrows
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

        if (account == null) {
            sender.sendMessage(strToComp("<red>Nie ma takiego gracza w bazie."));
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(args[1]);
        } catch (Exception exception) {
            sender.sendMessage(strToComp("<red>Użycie: <gray>" + getSyntax()));
            return;
        }

        account.setMoney(account.getMoney() + amount);
        plugin.getAccountDAO().update(account);
        sender.sendMessage(strToComp("<gray>Zabrano <red>-" + amount + "$</red> graczu: <aqua>"
                + playerName + "</aqua> z stanu konta"));
    }
}
