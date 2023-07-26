package pl.pomoku.survivalpomoku.database;

import pl.pomoku.survivalpomoku.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class PlayerDAO extends AbstractDAO<Player> {

    public PlayerDAO(DatabaseManager databaseManager) {
        super(databaseManager, "players");
    }

    @Override
    public void createTable() {
        try {
            if (!tableExists()) {
                openConnection();
                Statement statement = connection.createStatement();

                String createTableQuery = "CREATE TABLE " + table + " (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50))";
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
    public Player getById(int id) throws SQLException {
        openConnection();
        String query = "SELECT * FROM players WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        Player player = null;
        if (resultSet.next()) {
            player = new Player();
            player.setId(resultSet.getInt("id"));
            player.setName(resultSet.getString("name"));
        }

        resultSet.close();
        statement.close();
        closeConnection();
        return player;
    }

    @Override
    public void insert(Player player) throws SQLException {
        openConnection();
        String query = "INSERT INTO players (name) VALUES (?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, player.getName());
        statement.executeUpdate();
        statement.close();
        closeConnection();
    }

    @Override
    public void update(Player player) throws SQLException {
        openConnection();
        String query = "UPDATE players SET name = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, player.getName());
        statement.setInt(2, player.getId());

        statement.executeUpdate();
        statement.close();
        closeConnection();
    }

    @Override
    public void delete(Player player) throws SQLException {
        openConnection();
        String query = "DELETE FROM players WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, player.getId());
        statement.executeUpdate();
        statement.close();
        closeConnection();
    }
}
