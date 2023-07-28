package pl.pomoku.survivalpomoku.menusystem;

import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import pl.pomoku.pomokupluginsrepository.gui.PaginatedMenu;
import pl.pomoku.pomokupluginsrepository.gui.PlayerMenuUtility;
import pl.pomoku.survivalpomoku.entity.MarketItem;

import java.util.List;

import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

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

    @SneakyThrows
    @Override
    public void setMenuItems() {
        List<MarketItem> marketItemList = plugin.getMarketItemDAO().getAll();
        if (marketItemList.isEmpty()) return;

        super.addMenuBorder();
        previewsPage(48);
        nextPage(50, marketItemList.size());

        for (int i = 0; i < super.maxItemsPerPage; i++) {
            index = super.maxItemsPerPage * page + i;
            if (index >= marketItemList.size()) break;
            if (marketItemList.get(index) != null) {
                inventory.addItem(marketItemList.get(i).getItem());
            }
        }
    }
}
