package pl.pomoku.survivalpomoku.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class PlayerQuit implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent event) throws SQLException {
        plugin.getTimePlayerDAO().update(plugin.getTimeMoneyManager()
                .getTimePlayerCache().get(event.getPlayer().getUniqueId()));
        plugin.getTimeMoneyManager().removePlayerFromTimePlayerCache(event.getPlayer());
    }
}
