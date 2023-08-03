package pl.pomoku.survivalpomoku.database;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class DatabaseManager {
    private static final String DB_DRIVER = plugin.getDatabaseConfig().get().getString("database.driver");
    private static final String DB_HOST = plugin.getDatabaseConfig().get().getString("database.host");
    private static final String DB_PORT = plugin.getDatabaseConfig().get().getString("database.port");
    private static final String DB_DATABASE = plugin.getDatabaseConfig().get().getString("database.database");
    private static final String DB_URL = "jdbc:" + DB_DRIVER + "://" + DB_HOST + ":" + DB_PORT + "/" + DB_DATABASE;
    private static final String DB_USER = plugin.getDatabaseConfig().get().getString("database.user");
    private static final String DB_PASSWORD = plugin.getDatabaseConfig().get().getString("database.password");

    private Connection connection;

    public DatabaseManager() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
