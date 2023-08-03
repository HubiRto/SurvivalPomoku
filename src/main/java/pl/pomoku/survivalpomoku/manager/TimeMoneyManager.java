package pl.pomoku.survivalpomoku.manager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import pl.pomoku.survivalpomoku.SurvivalPomoku;
import pl.pomoku.survivalpomoku.entity.Account;
import pl.pomoku.survivalpomoku.entity.TimePlayer;
import redis.clients.jedis.Jedis;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

@Getter
public class TimeMoneyManager {

    private final BukkitScheduler scheduler;
    private final SurvivalPomoku plugin;
    private final Gson gson = new Gson();
    public TimeMoneyManager(SurvivalPomoku plugin) {
        this.plugin = plugin;
        this.scheduler = Bukkit.getScheduler();
    }

    private List<TimePlayer> getAllTimePlayersWithReceivedAllFalse() throws SQLException {
        // Get a Jedis connection from the pool
        try (Jedis jedis = plugin.getJedis().getResource()) {
            // Check if the list exists in Redis cache
            String timePlayersJson = jedis.get("timePlayersWithReceivedAllFalse");

            if (timePlayersJson != null) {
                // If found in cache, deserialize the JSON and return the list
                return deserializeTimePlayerList(timePlayersJson);
            }

            // If not found in Redis, fetch from the database
            List<TimePlayer> timePlayerList = plugin.getTimePlayerDAO().getAllWithReceivedAllFalse();

            // Cache the list in Redis for future use
            jedis.set("timePlayersWithReceivedAllFalse", serializeTimePlayerList(timePlayerList));

            return timePlayerList;
        }
    }

    private List<TimePlayer> getAllTimePlayers() throws SQLException {
        // Get a Jedis connection from the pool
        try (Jedis jedis = plugin.getJedis().getResource()) {
            // Check if the list exists in Redis cache
            String timePlayersJson = jedis.get("allTimePlayers");

            if (timePlayersJson != null) {
                // If found in cache, deserialize the JSON and return the list
                return deserializeTimePlayerList(timePlayersJson);
            }

            // If not found in Redis, fetch from the database
            List<TimePlayer> timePlayerList = plugin.getTimePlayerDAO().getAll();

            // Cache the list in Redis for future use
            jedis.set("allTimePlayers", serializeTimePlayerList(timePlayerList));

            return timePlayerList;
        }
    }

    private String serializeTimePlayerList(List<TimePlayer> timePlayerList) {
        return gson.toJson(timePlayerList);
    }

    private List<TimePlayer> deserializeTimePlayerList(String json) {
        return gson.fromJson(json, new TypeToken<List<TimePlayer>>() {}.getType());
    }

    /**
     * Inicjuje zarządzanie czasem i pieniędzmi.
     * Uruchamia zadania cykliczne dla sprawdzania czasu graczy i resetowania danych co minute.
     */
    public void init() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::checkAllPlayers, 0, 20 * 60);
        runResetTask();
    }

    /**
     * Uruchamia zadanie cykliczne dla resetu czasu.
     * Uruchamia zadanie co 60 sekund w celu sprawdzenia, czy czas minął od ostatniego resetu.
     */
    private void runResetTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, (this::checkResetTime), 0, 20 * 60);
    }

    /**
     * Sprawdza, czy nadszedł czas na reset dziennej liczby czasu graczy.
     * Jeśli czas od ostatniego resetu przekracza 24 godziny, resetuje licznik dziennej ilości czasu graczy.
     */
    private void checkResetTime() {
        long lastTime = getLastResetTime();

        if (lastTime == -1) {
            lastTime = getLastMidnightMillis();
            setLastResetTime(lastTime);
        }

        if (System.currentTimeMillis() - lastTime >= 86400000) {
            setLastResetTime(getLastResetTime());
            resetAllTimePlayersDailyTime();
        }
    }

    /**
     * Pobiera czas ostatniego resetu dziennej liczby czasu graczy.
     *
     * @return Czas ostatniego resetu.
     */
    private long getLastResetTime() {
        return plugin.getTimeIsMoneyConfig().get().getLong("lastResetTime", -1);
    }

    /**
     * Ustawia czas ostatniego resetu dziennej liczby czasu graczy.
     *
     * @param time Czas ostatniego resetu.
     */
    private void setLastResetTime(long time) {
        plugin.getTimeIsMoneyConfig().get().set("lastResetTime", time);
        plugin.getTimeIsMoneyConfig().save();
    }

    /**
     * Resetuje codzienny czas graczy.
     * Pobiera listę wszystkich graczy, resetuje ich dzienne czasy i aktualizuje dane w bazie danych.
     */
    private void resetAllTimePlayersDailyTime() {
        try {
            List<TimePlayer> timePlayerList = plugin.getTimePlayerDAO().getAll();
            timePlayerList.forEach(this::resetTimePlayerDailyTime);
            plugin.getTimePlayerDAO().batchUpdate(timePlayerList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resetuje dzienne czasy gracza.
     *
     * @param timePlayer Gracz, którego czas zostanie zresetowany.
     */
    private void resetTimePlayerDailyTime(TimePlayer timePlayer) {
        timePlayer.setTotalTime(timePlayer.getTotalTime() + timePlayer.getTodayTime());
        timePlayer.setTodayTime(0);
        timePlayer.setReceivedAll(false);
        timePlayer.setCollectedRewards(0);
    }

    /**
     * Pobiera czas w milisekundach dla ostatniej północy.
     *
     * @return Czas ostatniej północy w milisekundach.
     */
    private long getLastMidnightMillis() {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime lastMidnight = currentDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
        return lastMidnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Sprawdza czas graczy i dodaje im pieniądze za każde 15 minut online.
     */
    private void checkAllPlayers() {
        try {
            long startTime = System.nanoTime();
            List<TimePlayer> timePlayerList = getAllTimePlayersWithReceivedAllFalse();
            timePlayerList.forEach(this::updatePlayerTime);
            plugin.getTimePlayerDAO().batchUpdate(timePlayerList);

            long endTime = System.nanoTime() - startTime;
            plugin.getLogger().log(Level.INFO, "Czas wykonania (w ns): "
                    + (endTime) + "ns, (w ms): "
                    + TimeUnit.NANOSECONDS.toMillis(endTime) + "ms");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Aktualizuje czas gracza i przyznaje mu pieniądze za każde 15 minut online.
     *
     * @param timePlayer Gracz, którego czas zostanie zaktualizowany.
     */
    private void updatePlayerTime(TimePlayer timePlayer) {
        Player player = Bukkit.getPlayer(UUID.fromString(timePlayer.getPlayerUUID()));
        if (player != null && player.isOnline()) {
            long todayTime = timePlayer.getTodayTime() + 1;
            timePlayer.setTodayTime(todayTime);
            if (todayTime % 15 == 0) {
                addMoneyToAccount(player);
                if (increaseNumberOfRewards(timePlayer) >= 5) timePlayer.setReceivedAll(true);
            }
        }
    }

    /**
     * Dodaje pieniądze na konto gracza za 15 minut online.
     *
     * @param player Gracz, któremu zostaną dodane pieniądze.
     */
    private void addMoneyToAccount(Player player) {
        player.sendMessage(strToComp("<gray>Otrzymałeś <green>15$</green> za <yellow>15 min</yellow> gry"));
        try {
            Account account = plugin.getAccountDAO().getByPlayer(player);
            account.setMoney(account.getMoney() + 15);
            plugin.getAccountDAO().update(account);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zwiększa liczbę zdobytych nagród dla gracza.
     *
     * @param timePlayer Gracz, którego liczba zdobytych nagród zostanie zwiększona.
     * @return Aktualna liczba zdobytych nagród po zwiększeniu.
     */
    private int increaseNumberOfRewards(TimePlayer timePlayer) {
        int collectedRewards = timePlayer.getCollectedRewards() + 1;
        timePlayer.setCollectedRewards(collectedRewards);
        return collectedRewards;
    }
}
