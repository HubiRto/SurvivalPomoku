package pl.pomoku.survivalpomoku.database.dao;

import pl.pomoku.survivalpomoku.database.AbstractDAO;
import pl.pomoku.survivalpomoku.database.DatabaseManager;
import pl.pomoku.survivalpomoku.entity.TimePlayer;
import pl.pomoku.survivalpomoku.utils.base64.Base64ConvertException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class TimePlayerDAO extends AbstractDAO<TimePlayer> {
    public TimePlayerDAO(DatabaseManager databaseManager) {
        super(databaseManager, "time_player");
    }

    @Override
    public void createTable() {
        try {
            if (!tableExists()) {
                openConnection();
                Statement statement = connection.createStatement();

                String createTableQuery = "CREATE TABLE " + table + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "accountId INT, " +
                        "uuid VARCHAR(255), " +
                        "player_name VARCHAR(255), " +
                        "totalTime LONG, " +
                        "todayTime LONG, " +
                        "collectedRewards INT, " +
                        "receivedAll BOOLEAN" +
                        ")";
                statement.executeUpdate(createTableQuery);

                statement.close();
                closeConnection();
                plugin.getServer().getConsoleSender().sendMessage(strToComp("<yellow>Stworzono tabelę: <aqua>"
                        + table + " </aqua> w bazie danych."));
            } else {
                plugin.getServer().getConsoleSender().sendMessage(strToComp("<green>Wczytano tabelę: <aqua>"
                        + table + " </aqua> z bazy danych."));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TimePlayer getById(int id) throws SQLException, Base64ConvertException {
        return null;
    }

    @Override
    public void insert(TimePlayer timePlayer) throws SQLException {
        openConnection();
        String query = "INSERT INTO " + table + " (accountId, uuid, player_name, totalTime, todayTime, collectedRewards, receivedAll) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, timePlayer.getAccountId());
        statement.setString(2, timePlayer.getPlayerUUID());
        statement.setString(3, timePlayer.getPlayerName());
        statement.setLong(4, 0);
        statement.setLong(5, 0);
        statement.setInt(6, 0);
        statement.setBoolean(7, false);
        statement.executeUpdate();
        statement.close();
        closeConnection();
    }

    public List<TimePlayer> getAllWithReceivedAllFalse() throws SQLException {
        openConnection();

        List<TimePlayer> result = new ArrayList<>();
        String query = "SELECT * FROM " + table + " WHERE receivedAll = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        try {
            statement.setBoolean(1, false);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                TimePlayer timePlayer = new TimePlayer();
                timePlayer.setId(resultSet.getInt("id"));
                timePlayer.setAccountId(resultSet.getInt("accountId"));
                timePlayer.setPlayerUUID(resultSet.getString("uuid"));
                timePlayer.setPlayerName(resultSet.getString("player_name"));
                timePlayer.setTotalTime(resultSet.getLong("totalTime"));
                timePlayer.setTodayTime(resultSet.getLong("todayTime"));
                timePlayer.setCollectedRewards(resultSet.getInt("collectedRewards"));
                timePlayer.setReceivedAll(resultSet.getBoolean("receivedAll"));
                result.add(timePlayer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return result;
    }

    public List<TimePlayer> getAll() throws SQLException {
        openConnection();

        List<TimePlayer> result = new ArrayList<>();
        String query = "SELECT * FROM " + table;
        PreparedStatement statement = connection.prepareStatement(query);

        try {
            statement.setBoolean(1, false);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                TimePlayer timePlayer = new TimePlayer();
                timePlayer.setId(resultSet.getInt("id"));
                timePlayer.setAccountId(resultSet.getInt("accountId"));
                timePlayer.setPlayerUUID(resultSet.getString("uuid"));
                timePlayer.setPlayerName(resultSet.getString("player_name"));
                timePlayer.setTotalTime(resultSet.getLong("totalTime"));
                timePlayer.setTodayTime(resultSet.getLong("todayTime"));
                timePlayer.setCollectedRewards(resultSet.getInt("collectedRewards"));
                timePlayer.setReceivedAll(resultSet.getBoolean("receivedAll"));
                result.add(timePlayer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return result;
    }

    public void batchUpdate(List<TimePlayer> timePlayers) throws SQLException {
        openConnection();
        String query = "UPDATE " + table + " SET todayTime = ?, collectedRewards = ?, receivedAll = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        for (TimePlayer timePlayer : timePlayers) {
            statement.setLong(1, timePlayer.getTodayTime());
            statement.setInt(2, timePlayer.getCollectedRewards());
            statement.setBoolean(3, timePlayer.isReceivedAll());
            statement.setInt(4, timePlayer.getId());
            statement.addBatch();
        }
        statement.executeBatch();
        statement.close();
        closeConnection();
    }

    public void updateAllAsync(List<TimePlayer> timePlayers) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<CompletableFuture<Void>> futures = timePlayers.stream()
                .map(timePlayer -> CompletableFuture.runAsync(() -> {
                    try {
                        update(timePlayer);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }, executor))
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allFutures.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    @Override
    public void update(TimePlayer timePlayer) throws SQLException {
        openConnection();
        String query = "UPDATE " + table + " SET todayTime = ?, collectedRewards = ?, receivedAll = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(  1, timePlayer.getTodayTime());
        statement.setInt(2, timePlayer.getCollectedRewards());
        statement.setBoolean(3, timePlayer.isReceivedAll());
        statement.setInt(4, timePlayer.getId());
        statement.executeUpdate();
        statement.close();
        closeConnection();
    }

    @Override
    public void delete(TimePlayer obj) throws SQLException {

    }

    public TimePlayer getByPlayerUUID(String UUID) throws SQLException {
        openConnection();
        String query = "SELECT * FROM " + table + " WHERE uuid = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, UUID);
        ResultSet resultSet = statement.executeQuery();

        TimePlayer timePlayer = null;
        if (resultSet.next()) {
            timePlayer = new TimePlayer();
            timePlayer.setId(resultSet.getInt("id"));
            timePlayer.setAccountId(resultSet.getInt("accountId"));
            timePlayer.setPlayerUUID(resultSet.getString("uuid"));
            timePlayer.setPlayerName(resultSet.getString("player_name"));
            timePlayer.setTotalTime(resultSet.getLong("totalTime"));
            timePlayer.setTodayTime(resultSet.getLong("todayTime"));
            timePlayer.setCollectedRewards(resultSet.getInt("collectedRewards"));
            timePlayer.setReceivedAll(resultSet.getBoolean("receivedAll"));
        }

        resultSet.close();
        statement.close();
        closeConnection();
        return timePlayer;
    }
}
