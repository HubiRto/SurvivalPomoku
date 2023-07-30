package pl.pomoku.survivalpomoku.commands.timeIsMoneyCmd.subCmd;

import lombok.SneakyThrows;
import org.bukkit.command.CommandSender;
import pl.pomoku.survivalpomoku.commandManagerLib.SubCommand;
import pl.pomoku.survivalpomoku.entity.TimePlayer;

import java.util.List;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class ResetTimeSub implements SubCommand {
    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "Resetuje ilość otrzymanych nagród na 0";
    }

    @Override
    public String getSyntax() {
        return "/timeismoney reset";
    }

    @Override
    public String getPermission() {
        return "timeismoney.reset";
    }

    @Override
    public List<String> getTabCompletion(int index, String[] args) {
        return null;
    }

    @Override
    @SneakyThrows
    public void perform(CommandSender sender, String[] args) {
        for(TimePlayer timePlayer : plugin.getTimePlayerDAO().getAll()){
            timePlayer.setReceivedAll(false);
            timePlayer.setTodayTime(0);
            timePlayer.setCollectedRewards(0);
            plugin.getTimePlayerDAO().update(timePlayer);
        }
        sender.sendMessage(strToComp("<green>Pomyślnie zresetowano zdobyte nagrody przez graczy."));
    }
}
