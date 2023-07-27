package pl.pomoku.survivalpomoku;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import pl.pomoku.pomokupluginsrepository.commands.EasyCommand;
import pl.pomoku.survivalpomoku.commandManagerLib.MainCommand;
import pl.pomoku.survivalpomoku.commands.moneyCmd.MoneyMainCmd;
import pl.pomoku.survivalpomoku.database.DatabaseManager;
import pl.pomoku.survivalpomoku.database.AccountDAO;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public final class SurvivalPomoku extends JavaPlugin {
    private DatabaseManager databaseManager;
    private AccountDAO accountDAO;
    public static SurvivalPomoku plugin;
    private MainCommand moneyCmd;

    @Override
    public void onEnable() {
        plugin = this;
        databaseManager = new DatabaseManager();
        accountDAO = new AccountDAO(databaseManager);
        accountDAO.createTable();

        moneyCmd = new MoneyMainCmd();
        moneyCmd.registerMainCommand(this, "money");

        loadListeners();

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        // Pobieramy aktualną godzinę
        long currentTimeMillis = System.currentTimeMillis();

        // Ustawiamy czas, o którym chcemy, aby program się uruchomił (12:23)
        long targetTimeMillis = getTargetTimeMillis(currentTimeMillis, 17, 33);

        // Jeśli docelowy czas już minął dzisiaj, to ustawiamy go na następny dzień
        if (targetTimeMillis <= currentTimeMillis) {
            targetTimeMillis = getTargetTimeMillis(targetTimeMillis + TimeUnit.DAYS.toMillis(1), 12, 23);
        }

        // Obliczamy różnicę czasu między teraz a docelowym czasem
        long initialDelayMillis = targetTimeMillis - currentTimeMillis;

        // Ustawiamy zadanie, które będzie wypisywało wiadomość "Witaj, świecie!" co 24h (86400 sekund)
        executorService.scheduleAtFixedRate(() -> System.out.println("Witaj, świecie!"), initialDelayMillis, 86400, TimeUnit.SECONDS);
    }


    private static long getTargetTimeMillis(long currentTimeMillis, int hour, int minute) {
        long targetTimeMillis = currentTimeMillis;
        targetTimeMillis = targetTimeMillis - targetTimeMillis % TimeUnit.DAYS.toMillis(1); // Wyzeruj czas do północy
        targetTimeMillis += TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute); // Dodaj godzinę i minutę
        return targetTimeMillis;
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
