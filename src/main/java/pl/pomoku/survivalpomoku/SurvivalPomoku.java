package pl.pomoku.survivalpomoku;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Async;
import org.reflections.Reflections;
import pl.pomoku.pomokupluginsrepository.gui.PlayerMenuUtility;
import pl.pomoku.survivalpomoku.commandManagerLib.MainCommand;
import pl.pomoku.survivalpomoku.commands.marketCmd.MarketMainCmd;
import pl.pomoku.survivalpomoku.commands.moneyCmd.MoneyMainCmd;
import pl.pomoku.survivalpomoku.commands.timeIsMoneyCmd.TimeIsMoneyMainCmd;
import pl.pomoku.survivalpomoku.database.dao.AccountDAO;
import pl.pomoku.survivalpomoku.database.DatabaseManager;
import pl.pomoku.survivalpomoku.database.dao.MarketItemDAO;
import pl.pomoku.survivalpomoku.database.dao.TimePlayerDAO;
import pl.pomoku.survivalpomoku.manager.TimeMoneyManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;

@Getter
public final class SurvivalPomoku extends JavaPlugin {
    //DATABASE
    private DatabaseManager databaseManager;

    //DATABASE - DAO
    private AccountDAO accountDAO;
    private MarketItemDAO marketItemDAO;
    private TimePlayerDAO timePlayerDAO;

    //COMMANDS
    private MainCommand moneyCmd;
    private MainCommand marketCmd;
    private MainCommand timeIsMoneyCmd;

    public static SurvivalPomoku plugin;
    private TimeMoneyManager timeMoneyManager;
    private final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;

        databaseManager = new DatabaseManager();

        accountDAO = new AccountDAO(databaseManager);
        accountDAO.createTable();

        marketItemDAO = new MarketItemDAO(databaseManager);
        marketItemDAO.createTable();

        timePlayerDAO = new TimePlayerDAO(databaseManager);
        timePlayerDAO.createTable();

        moneyCmd = new MoneyMainCmd();
        moneyCmd.registerMainCommand(this, "money");

        marketCmd = new MarketMainCmd();
        marketCmd.registerMainCommand(this, "market");

        timeIsMoneyCmd = new TimeIsMoneyMainCmd();
        timeIsMoneyCmd.registerMainCommand(this, "timeismoney");

        loadListeners();

        timeMoneyManager = new TimeMoneyManager();
        timeMoneyManager.init();
    }

    @Override
    public void onDisable() {
        timeMoneyManager.saveAll();
        try {
            databaseManager.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadListeners() {
        String packageName = getClass().getPackage().getName();
        for (Class<?> clazz : new Reflections(packageName + ".listeners").getSubTypesOf(Listener.class)) {
            try {
                Listener listener = (Listener) clazz.getDeclaredConstructor().newInstance();
                getServer().getPluginManager().registerEvents(listener, this);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public PlayerMenuUtility getPlayerMenuUtility(Player player) {
        PlayerMenuUtility playerMenuUtility;
        if (playerMenuUtilityMap.containsKey(player)) return playerMenuUtilityMap.get(player);
        playerMenuUtility = new PlayerMenuUtility(player);
        playerMenuUtilityMap.put(player, playerMenuUtility);
        return playerMenuUtility;
    }
}
