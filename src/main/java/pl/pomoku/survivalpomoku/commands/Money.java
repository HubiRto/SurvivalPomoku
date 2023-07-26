package pl.pomoku.survivalpomoku.commands;

import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import pl.pomoku.pomokupluginsrepository.commands.CommandInfo;
import pl.pomoku.pomokupluginsrepository.commands.EasyCommand;
import pl.pomoku.survivalpomoku.Account;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

@CommandInfo(name = "money", requiresPlayer = true)
public class Money extends EasyCommand {
    @SneakyThrows
    @Override
    public void execute(Player p, String[] args) {
        if(args.length == 0){
            Account account = plugin.getAccountDAO().getByPlayer(p);
            p.sendMessage(strToComp("<gray>Masz na koncie: <green> " + account.getMoney() + "$"));
        }
    }
}
