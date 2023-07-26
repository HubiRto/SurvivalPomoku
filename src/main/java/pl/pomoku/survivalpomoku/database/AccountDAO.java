package pl.pomoku.survivalpomoku.database;

import org.bukkit.entity.Player;
import pl.pomoku.survivalpomoku.Account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class AccountDAO extends AbstractDAO<Account> {

    public AccountDAO(DatabaseManager databaseManager) {
        super(databaseManager, "account");
    }

    @Override
    public void createTable() {
        try {
            if (!tableExists()) {
                openConnection();
                Statement statement = connection.createStatement();

                String createTableQuery = "CREATE TABLE " + table + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "uuid VARCHAR(255), " +
                        "money DOUBLE " +
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
    public Account getById(int id) throws SQLException {
        openConnection();
        String query = "SELECT * FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        Account account = null;
        if (resultSet.next()) {
            account = new Account();
            account.setId(resultSet.getInt("id"));
            account.setUuid(resultSet.getString("uuid"));
            account.setMoney(resultSet.getDouble("money"));
        }

        resultSet.close();
        statement.close();
        closeConnection();
        return account;
    }

    public Account getByPlayer(Player player) throws SQLException {
        openConnection();
        String query = "SELECT * FROM " + table + " WHERE uuid = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, player.getUniqueId().toString());
        ResultSet resultSet = statement.executeQuery();

        Account account = null;
        if (resultSet.next()) {
            account = new Account();
            account.setId(resultSet.getInt("id"));
            account.setUuid(resultSet.getString("uuid"));
            account.setMoney(resultSet.getDouble("money"));
        }

        resultSet.close();
        statement.close();
        closeConnection();
        return account;
    }

    @Override
    public void insert(Account player) throws SQLException {
        openConnection();
        String query = "INSERT INTO " + table + " (uuid, money) VALUES (?,?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, player.getUuid());
        statement.setDouble(2, player.getMoney());
        statement.executeUpdate();
        statement.close();
        closeConnection();
    }

    @Override
    public void update(Account player) throws SQLException {
        openConnection();
        String query = "UPDATE " + table + " SET money = ? WHERE uuid = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setDouble(1, player.getMoney());
        statement.setString(2, player.getUuid());

        statement.executeUpdate();
        statement.close();
        closeConnection();
    }

    @Override
    public void delete(Account player) throws SQLException {
        openConnection();
        String query = "DELETE FROM " + table + " WHERE uuid = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, player.getUuid());
        statement.executeUpdate();
        statement.close();
        closeConnection();
    }
}
