package pl.pomoku.survivalpomoku.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractDAO<T> {
    protected Connection connection;
    protected DatabaseManager databaseManager;
    protected String table;

    public AbstractDAO(DatabaseManager databaseManager, String table) {
        this.databaseManager = databaseManager;
        this.table = table;
    }

    public void openConnection() throws SQLException {
        connection = databaseManager.getConnection();
    }

    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
    public abstract void createTable();

    public abstract T getById(int id) throws SQLException;

    public abstract void insert(T obj) throws SQLException;

    public abstract void update(T obj) throws SQLException;

    public abstract void delete(T obj) throws SQLException;

    protected boolean tableExists() throws SQLException {
        openConnection();
        ResultSet resultSet = connection.getMetaData().getTables(null, null, table, null);
        boolean exists = resultSet.next();
        resultSet.close();
        closeConnection();
        return exists;
    }
}
