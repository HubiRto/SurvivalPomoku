package pl.pomoku.survivalpomoku;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import pl.pomoku.pomokupluginsrepository.commands.EasyCommand;
import pl.pomoku.survivalpomoku.commandManagerLib.MainCommand;
import pl.pomoku.survivalpomoku.commands.moneyCmd.MoneyMainCmd;
import pl.pomoku.survivalpomoku.database.AccountDAO;
import pl.pomoku.survivalpomoku.database.DatabaseManager;
import pl.pomoku.survivalpomoku.manager.TimeMoneyManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Objects;

@Getter
public final class SurvivalPomoku extends JavaPlugin {
    private DatabaseManager databaseManager;
    private AccountDAO accountDAO;
    public static SurvivalPomoku plugin;
    private MainCommand moneyCmd;
    private TimeMoneyManager timeMoneyManager;

    @Override
    public void onEnable() {
        plugin = this;
        databaseManager = new DatabaseManager();
        accountDAO = new AccountDAO(databaseManager);
        accountDAO.createTable();

        moneyCmd = new MoneyMainCmd();
        moneyCmd.registerMainCommand(this, "money");

        loadListeners();

        timeMoneyManager = new TimeMoneyManager();
        timeMoneyManager.init();
    }

    @Override
    public void onDisable() {
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

    private void loadCommands() {
        String packageName = getClass().getPackage().getName();
        for (Class<? extends EasyCommand> clazz : new Reflections(packageName + ".commands").getSubTypesOf(EasyCommand.class)) {
            try {
                EasyCommand pluginCommand = clazz.getDeclaredConstructor().newInstance();
                Objects.requireNonNull(getCommand(pluginCommand.getCommandInfo().name())).setExecutor(pluginCommand);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
