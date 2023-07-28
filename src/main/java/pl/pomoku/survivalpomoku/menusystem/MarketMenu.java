package pl.pomoku.survivalpomoku.menusystem;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import pl.pomoku.pomokupluginsrepository.gui.PaginatedMenu;
import pl.pomoku.pomokupluginsrepository.gui.PlayerMenuUtility;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;

public class MarketMenu extends PaginatedMenu {
    public MarketMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public int setMaxItemsPerPage() {
        return 14;
    }

    @Override
    public Component getMenuName() {
        return strToComp("<dark_gray><bold>STRONA</bold>: <aqua>" + page);
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public void closeHandleMenu(InventoryCloseEvent inventoryCloseEvent) {

    }

    @Override
    public void setMenuItems() {
        previewsPage(14);
        nextPage(15, 3);
    }
}
