package pl.pomoku.survivalpomoku.manager;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.pomoku.survivalpomoku.entity.Account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

@Getter
public class TimeMoneyManager {
    private final HashMap<Player, Integer> playerGameTime;
    private final HashMap<Player, Integer> playerCollectedReward;
    private final List<Player> receivedMessage;
    private BukkitRunnable dailyResetTask;

    public TimeMoneyManager() {
        this.playerGameTime = new HashMap<>();
        this.playerCollectedReward = new HashMap<>();
        this.receivedMessage = new ArrayList<>();
        this.scheduleDailyReset();
    }

    public void playerJoin(Player player) {
        if (playerGameTime.containsKey(player)) return;
        playerGameTime.put(player, 0);
        playerCollectedReward.put(player, 0);
    }

    public void init() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::checkAllPlayers, 0, 20 * 60);
    }

    @SneakyThrows
    private void checkAllPlayers() {
        for(Player player : playerGameTime.keySet()){
            if(player.isOnline()) {
                if (playerCollectedReward.get(player) < 5) {
                    playerGameTime.put(player, playerGameTime.get(player) + 1);
                    if (playerGameTime.get(player) % 15 == 0) {
                        player.sendMessage(strToComp("<gray>Otrzymałeś <green>15$</green> za <yellow>15 min</yellow> gry"));

                        Account account = plugin.getAccountDAO().getByPlayer(player);
                        account.setMoney(account.getMoney() + 15);
                        plugin.getAccountDAO().update(account);

                        playerCollectedReward.put(player, playerCollectedReward.get(player) + 1);
                    }
                }else {
                    if(!receivedMessage.contains(player)){
                        player.sendMessage(strToComp("<red>Wykorzystałeś dzienny limi nagród."));
                        receivedMessage.add(player);
                    }
                }
            }
        }
    }

    private void scheduleDailyReset() {
        dailyResetTask = new BukkitRunnable() {
            @Override
            public void run() {
                resetDailyRewards();
            }
        };

        long currentTime = System.currentTimeMillis();
        long nextMidnight = (currentTime / 86400000L + 1) * 86400000L;
        long delay = (nextMidnight - currentTime) / 50L;

        dailyResetTask.runTaskTimer(plugin, delay, 1728000L);
    }

    private void resetDailyRewards() {
        receivedMessage.clear();
        playerCollectedReward.replaceAll((player, value) -> 0);
    }
}
