package pl.pomoku.survivalpomoku.manager;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.pomoku.survivalpomoku.entity.Account;
import pl.pomoku.survivalpomoku.entity.TimePlayer;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

@Getter
public class TimeMoneyManager {
    private final HashMap<UUID, TimePlayer> timePlayerCache;

    public TimeMoneyManager() {
        timePlayerCache = new HashMap<>();
        try {
            for (TimePlayer timePlayer : plugin.getTimePlayerDAO().getAllWithReceivedAllFalse()) {
                Player player = Bukkit.getPlayer(UUID.fromString(timePlayer.getPlayerUUID()));
                if (player != null && player.isOnline()) {
                    UUID playerUUID = player.getUniqueId();
                    timePlayerCache.put(playerUUID, timePlayer);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPlayerToTimePlayerCache(Player player) {
        TimePlayer timePlayerFromDB;

        try {
            timePlayerFromDB = plugin.getTimePlayerDAO().getByPlayerUUID(player.getUniqueId().toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (timePlayerFromDB == null) return;
        if(timePlayerFromDB.isReceivedAll()) return;
        timePlayerCache.put(player.getUniqueId(), timePlayerFromDB);
    }

    public void removePlayerFromTimePlayerCache(Player player) {
        timePlayerCache.remove(player.getUniqueId());
    }

    public void saveAll() {
        try {
            for(TimePlayer timePlayer : timePlayerCache.values()){
                plugin.getTimePlayerDAO().update(timePlayer);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void init() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::checkAllPlayers, 0, 20 * 20);
    }

    private void checkAllPlayers() {
        try {
            long startTime = System.nanoTime();

            for (TimePlayer timePlayer : timePlayerCache.values()) {
                Player player = Bukkit.getPlayer(UUID.fromString(timePlayer.getPlayerUUID()));
                if (player != null && player.isOnline()) {
                    long todayTime = timePlayer.getTodayTime() + 1;
                    timePlayer.setTodayTime(todayTime);
                    if (todayTime % 15 == 0) {
                        player.sendMessage(strToComp("<gray>Otrzymałeś <green>15$</green> za <yellow>15 min</yellow> gry"));

                        try {
                            Account account = plugin.getAccountDAO().getByPlayer(player);
                            account.setMoney(account.getMoney() + 15);
                            plugin.getAccountDAO().update(account);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        int collectedRewards = timePlayer.getCollectedRewards() + 1;
                        timePlayer.setCollectedRewards(collectedRewards);
                        if (collectedRewards == 5){
                            timePlayer.setReceivedAll(true);
                            plugin.getTimePlayerDAO().update(timePlayer);
                            timePlayerCache.remove(player.getUniqueId());
                        }
                    }
                    timePlayerCache.put(player.getUniqueId(), timePlayer);
                }
            }

            long endTime = System.nanoTime() - startTime;
            plugin.getLogger().log(Level.INFO, "Czas wykonania (w ns): "
                    + (endTime) + "ns, (w ms): "
                    + TimeUnit.NANOSECONDS.toMillis(endTime) + "ms");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
