package pl.pomoku.survivalpomoku.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.pomoku.survivalpomoku.entity.Account;
import pl.pomoku.survivalpomoku.entity.TimePlayer;

import java.sql.SQLException;

import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();

        Account account = plugin.getAccountDAO().getByPlayer(player);
        if(account == null){
            account = new Account();
            account.setMoney(0.0);
            account.setUuid(player.getUniqueId().toString());
            plugin.getAccountDAO().insert(account);

            TimePlayer timePlayer = TimePlayer.builder()
                    .accountId(plugin.getAccountDAO().getByPlayer(player).getId())
                    .playerUUID(player.getUniqueId().toString())
                    .playerName(player.getName())
                    .build();

            plugin.getTimePlayerDAO().insert(timePlayer);
        }

        plugin.getTimeMoneyManager().addPlayerToTimePlayerCache(player);
    }
}
