package pl.pomoku.survivalpomoku.commands;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.pomoku.pomokupluginsrepository.commands.CommandInfo;
import pl.pomoku.pomokupluginsrepository.commands.EasyCommand;
import pl.pomoku.survivalpomoku.Account;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

@CommandInfo(name = "money", requiresPlayer = true)
@SuppressWarnings("unused")
public class Money extends EasyCommand {
    @SneakyThrows
    @Override
    public void execute(Player p, String[] args) {
        if (args.length == 0) {
            Account account = plugin.getAccountDAO().getByPlayer(p);
            String color = account.getMoney() > 0 ? "<green>" : "<red>";
            p.sendMessage(strToComp("<gray>Masz na koncie: " + color + account.getMoney() + "$"));
        } else if (args.length == 2 && p.hasPermission("money.admin")) {
            if (args[0].equals("clear")) {
                String playerName = args[1];
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
        } else if (args.length == 3 && p.hasPermission("money.admin")) {
            if (args[0].equals("take")) {
                String playerName = args[1];
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

                double amount = Double.parseDouble(args[2]);

                account.setMoney(account.getMoney() - amount);
                plugin.getAccountDAO().update(account);
                p.sendMessage(strToComp("<gray>Zabrano <red>-" + amount + "$</red> graczu: <aqua>" + playerName + "</aqua> z stanu konta"));

            } else if (args[0].equals("add")) {
                String playerName = args[1];
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

                double amount = Double.parseDouble(args[2]);

                account.setMoney(account.getMoney() + amount);
                plugin.getAccountDAO().update(account);
                p.sendMessage(strToComp("<gray>Dodano <green>+" + amount + "$</green> graczu: <aqua>" + playerName + "</aqua> do stanu konta"));

            }
        }
    }
}
