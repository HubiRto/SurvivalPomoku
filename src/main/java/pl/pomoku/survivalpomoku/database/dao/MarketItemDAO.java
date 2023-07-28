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
                        "price DOUBLE " +
                        "expired_date DATE " +
                        "base64_item DATE " +
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

    @Override
    public void insert(MarketItem item) throws SQLException, Base64ConvertException {
        openConnection();
        String query = "INSERT INTO " + table + " (uuid, price, expired_date, base64_item) VALUES (?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, item.getUuid());
        statement.setDouble(2, item.getPrice());
        statement.setDate(3, item.getExpiredDate());
        statement.setString(4, Base64ItemStack.encode(item.getItem()));
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
        String query = "DELETE FROM " + table + " WHERE uuid = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, item.getUuid());
        statement.executeUpdate();
        statement.close();
        closeConnection();
    }
}
