package pl.pomoku.survivalpomoku.database.dao;

import pl.pomoku.survivalpomoku.database.AbstractDAO;
import pl.pomoku.survivalpomoku.database.DatabaseManager;
import pl.pomoku.survivalpomoku.entity.MarketItem;
import pl.pomoku.survivalpomoku.utils.base64.Base64ConvertException;
import pl.pomoku.survivalpomoku.utils.base64.Base64ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class MarketItemDAO extends AbstractDAO<MarketItem> {
    public MarketItemDAO(DatabaseManager databaseManager) {
        super(databaseManager, "market_item");
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
                        "player_name VARCHAR(255), " +
                        "price DOUBLE, " +
                        "expired_date DATE, " +
                        "base64_item TEXT " +
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
    public MarketItem getById(int id) throws SQLException, Base64ConvertException {
        openConnection();
        String query = "SELECT * FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        MarketItem item = null;
        if (resultSet.next()) {
            item = MarketItem.builder()
                    .id(resultSet.getInt("id"))
                    .uuid(resultSet.getString("uuid"))
                    .player_name(resultSet.getString("player_name"))
                    .price(resultSet.getDouble("price"))
                    .expiredDate(resultSet.getDate("expired_date"))
                    .item(Base64ItemStack.decode(resultSet.getString("base64_item")))
                    .build();
        }

        resultSet.close();
        statement.close();
        closeConnection();
        return item;
    }

    public List<MarketItem> getAll() throws SQLException, Base64ConvertException {
        openConnection();
        String query = "SELECT * FROM " + table;
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        List<MarketItem> itemList = new ArrayList<>();
        while (resultSet.next()) {
            itemList.add(MarketItem.builder()
                    .id(resultSet.getInt("id"))
                    .uuid(resultSet.getString("uuid"))
                    .player_name(resultSet.getString("player_name"))
                    .price(resultSet.getDouble("price"))
                    .expiredDate(resultSet.getDate("expired_date"))
                    .item(Base64ItemStack.decode(resultSet.getString("base64_item")))
                    .build());
        }
        resultSet.close();
        statement.close();
        return itemList;
    }

    @Override
    public void insert(MarketItem item) throws SQLException, Base64ConvertException {
        openConnection();
        String query = "INSERT INTO " + table + " (uuid, player_name, price, expired_date, base64_item) VALUES (?,?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, item.getUuid());
        statement.setString(2, item.getPlayer_name());
        statement.setDouble(3, item.getPrice());
        statement.setDate(4, item.getExpiredDate());
        statement.setString(5, Base64ItemStack.encode(item.getItem()));
        statement.executeUpdate();
        statement.close();
        closeConnection();
    }

    @Override
    public void update(MarketItem obj) throws SQLException {

    }

    @Override
    public void delete(MarketItem item) throws SQLException {
        openConnection();
        String query = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, item.getId());
        statement.executeUpdate();
        statement.close();
        closeConnection();
    }
}
