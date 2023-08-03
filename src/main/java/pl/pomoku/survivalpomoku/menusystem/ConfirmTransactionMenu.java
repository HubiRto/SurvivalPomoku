package pl.pomoku.survivalpomoku.menusystem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import pl.pomoku.pomokupluginsrepository.gui.Menu;
import pl.pomoku.pomokupluginsrepository.gui.PlayerMenuUtility;
import pl.pomoku.pomokupluginsrepository.items.ItemBuilder;
import pl.pomoku.survivalpomoku.entity.Account;
import pl.pomoku.survivalpomoku.entity.MarketItem;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;
import static pl.pomoku.survivalpomoku.menusystem.MarketMenu.calTimeBetweenTwoDates;

public class ConfirmTransactionMenu extends Menu {
    private final MarketItem marketItem;
    private final Account bayer;
    public ConfirmTransactionMenu(PlayerMenuUtility playerMenuUtility, MarketItem marketItem, Account bayer) {
        super(playerMenuUtility);
        this.marketItem = marketItem;
        this.bayer = bayer;
    }

    @Override
    public Component getMenuName() {
        return strToComp("<dark_gray>Czy na pewno chcesz kupić?");
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent inventoryClickEvent) {
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        switch (inventoryClickEvent.getSlot()){
            case 2 -> new MarketMenu(playerMenuUtility).open();
            case 6 -> {
                bayer.setMoney(bayer.getMoney() - marketItem.getPrice());

                try {
                    plugin.getAccountDAO().update(bayer);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                String itemOwnerUUID = marketItem.getUuid();
                Account itemOwnerAccount;

                try {
                    itemOwnerAccount = plugin.getAccountDAO().getByPlayerUUID(itemOwnerUUID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if(itemOwnerAccount == null) return;
                itemOwnerAccount.setMoney(itemOwnerAccount.getMoney() + marketItem.getPrice());

                try {
                    plugin.getAccountDAO().update(itemOwnerAccount);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                try {
                    plugin.getMarketItemDAO().delete(marketItem);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                player.getInventory().addItem(marketItem.getItem());
                player.sendMessage(strToComp("<gray>Zakupiłeś przedmiot za: <green>" + marketItem.getPrice()
                        + "$</green> od gracza: <light_purple>" + marketItem.getPlayer_name()));
                new MarketMenu(playerMenuUtility).open();
            }
        }
    }



    @Override
    public void closeHandleMenu(InventoryCloseEvent inventoryCloseEvent) {

    }

    @Override
    public void setMenuItems() {
        inventory.setItem(2, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .displayname(strToComp("<red><bold>ANULUJ"))
                .build());

        inventory.setItem(6, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .displayname(strToComp("<green><bold>POTWIERDZAM"))
                .build());

        List<Component> lore = List.of(
                strToComp("<gray>Cena: <green>" + marketItem.getPrice() + "<bold>$"),
                strToComp("<gray>Cena za szt: <green>" + (marketItem.getPrice() / marketItem.getItem().getAmount()) + "<bold>$"),
                strToComp("<gray>Sprzedający: <gold>" + marketItem.getPlayer_name()),
                strToComp("<gray>Wygasa za: <red>" + calTimeBetweenTwoDates(new Date(System.currentTimeMillis()), marketItem.getExpiredDate())),
                strToComp(" "),
                strToComp("<green>Kliknij prawym, aby zakupić"));

        inventory.setItem(4, new ItemBuilder(marketItem.getItem()).lore(lore).build());

        for(int slotId = 0; slotId < this.getSlots(); slotId++){
            ItemStack itemOnSlot = inventory.getItem(slotId);
            if(itemOnSlot == null) inventory.setItem(slotId, FILLER_GLASS);
        }
    }
}
