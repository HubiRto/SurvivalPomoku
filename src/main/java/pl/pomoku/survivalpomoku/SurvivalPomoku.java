package pl.pomoku.survivalpomoku;

import org.bukkit.plugin.java.JavaPlugin;
import pl.pomoku.survivalpomoku.database.DatabaseManager;
import pl.pomoku.survivalpomoku.database.PlayerDAO;

import java.sql.SQLException;

public final class SurvivalPomoku extends JavaPlugin {
    private DatabaseManager databaseManager;
    private PlayerDAO playerDAO;
    public static SurvivalPomoku plugin;
    @Override
    public void onEnable() {
        plugin = this;
        databaseManager = new DatabaseManager();
        playerDAO = new PlayerDAO(databaseManager);
        playerDAO.createTable();

        try {
            int playerId = 1;
            Player player = playerDAO.getById(playerId);
            if (player == null) {
                player = new Player();
                player.setId(playerId);
                player.setName("John");
                playerDAO.insert(player);
            } else {
                player.setName("UpdatedName");
                playerDAO.update(player);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        try {
            databaseManager.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
