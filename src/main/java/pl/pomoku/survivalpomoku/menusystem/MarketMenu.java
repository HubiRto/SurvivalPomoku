package pl.pomoku.survivalpomoku.menusystem;

import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.pomoku.pomokupluginsrepository.gui.PaginatedMenu;
import pl.pomoku.pomokupluginsrepository.gui.PlayerMenuUtility;
import pl.pomoku.pomokupluginsrepository.items.ItemBuilder;
import pl.pomoku.survivalpomoku.entity.Account;
import pl.pomoku.survivalpomoku.entity.MarketItem;
import pl.pomoku.survivalpomoku.enums.SortType;
import pl.pomoku.survivalpomoku.utils.base64.Base64ConvertException;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static org.bukkit.Material.*;
import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class MarketMenu extends PaginatedMenu {
    private SortType sortType = SortType.NORMAL;
    private final NamespacedKey namespacedKey = new NamespacedKey(plugin, "id");

    public MarketMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public int setMaxItemsPerPage() {
        return 28;
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
        if (!(inventoryClickEvent.getWhoClicked() instanceof Player player)) return;

        int slot = inventoryClickEvent.getSlot();

        if (slot > 9 && slot < 44) {
            ItemStack itemStack = inventoryClickEvent.getCurrentItem();

            if (itemStack == null) return;

            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta == null) return;
            if (!itemMeta.getPersistentDataContainer().has(namespacedKey)) return;

            Integer id = itemStack.getItemMeta().getPersistentDataContainer().get(
                    namespacedKey,
                    PersistentDataType.INTEGER
            );

            if (id == null) return;

            MarketItem marketItem;
            try {
                marketItem = plugin.getMarketItemDAO().getById(id);
            } catch (Base64ConvertException | SQLException e) {
                throw new RuntimeException(e);
            }

            if (marketItem == null) super.open();

            Account account;
            try {
                account = plugin.getAccountDAO().getByPlayer(player);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (account == null) return;

            double price = marketItem.getPrice();

            if (account.getMoney() < price) {
                player.sendMessage(strToComp("<red>Nie masz wystarczająco pieniędzy."));
                return;
            }

            if (!checkFreeSlots(player)){
                player.sendMessage(strToComp("<red>Nie masz wolego miejsca w ekwipunku."));
                return;
            }

            new ConfirmTransactionMenu(playerMenuUtility, marketItem, account).open();
        }

        switch (inventoryClickEvent.getSlot()) {
            case 49 -> {
                sortType = SortType.next();
                super.open();
            }
        }
    }

    private boolean checkFreeSlots(Player player) {
        return (int) Arrays.stream(player.getInventory().getContents())
                .filter(itemStack -> itemStack == null || itemStack.getType() == AIR)
                .count() > 0;
    }

    @Override
    public void closeHandleMenu(InventoryCloseEvent inventoryCloseEvent) {

    }

    @SneakyThrows
    @Override
    public void setMenuItems() {
        List<MarketItem> marketItemList = plugin.getMarketItemDAO().getAll();

        switch (sortType) {
            case LOWEST_PRICE -> marketItemList.sort(Comparator.comparingDouble(MarketItem::getPrice));
            case HIGHEST_PRICE -> marketItemList.sort(Comparator.comparingDouble(MarketItem::getPrice).reversed());
            case LATEST -> marketItemList.sort(Comparator.comparing(MarketItem::getExpiredDate));
            case OLDEST -> marketItemList.sort(Comparator.comparing(MarketItem::getExpiredDate).reversed());
        }

        super.addMenuBorder();
        previewsPage(48);
        nextPage(50, marketItemList.size());

        inventory.setItem(45, new ItemBuilder(PLAYER_HEAD).displayname(strToComp("<green>Twoje oferty")).build());
        inventory.setItem(49, new ItemBuilder(PAPER).displayname(strToComp("<gray>Sortowanie: " + SortType.names.get(sortType))).build());
        inventory.setItem(53, new ItemBuilder(BOOK).displayname(strToComp("<green>Informacja")).lore(List.of(strToComp("<gray>Tutaj znajdują się wszystkie przedmioty,"), strToComp("<gray>które wystawiają i kupują gracze."))).build());

        IntStream.range(44, 54)
                .filter(i -> inventory.getItem(i) == null)
                .forEach(i -> inventory.setItem(i, FILLER_GLASS));

        if(marketItemList.isEmpty()) return;

        for (int i = 0; i < super.maxItemsPerPage; i++) {
            index = super.maxItemsPerPage * page + i;
            if (index >= marketItemList.size()) break;
            if (marketItemList.get(index) != null) {
                List<Component> lore = List.of(
                        strToComp("<gray>Cena: <green>" + marketItemList.get(i).getPrice() + "<bold>$"),
                        strToComp("<gray>Cena za szt: <green>" + (marketItemList.get(i).getPrice() / marketItemList.get(i).getItem().getAmount()) + "<bold>$"),
                        strToComp("<gray>Sprzedający: <gold>" + marketItemList.get(i).getPlayer_name()),
                        strToComp("<gray>Wygasa za: <red>" + calTimeBetweenTwoDates(new Date(System.currentTimeMillis()), marketItemList.get(i).getExpiredDate())),
                        strToComp(" "),
                        strToComp("<green>Kliknij prawym, aby zakupić"));


                ItemStack item = new ItemBuilder(marketItemList.get(i).getItem())
                        .lore(lore).flag(ItemFlag.HIDE_ATTRIBUTES).build();
                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "id"),
                        PersistentDataType.INTEGER,
                        marketItemList.get(i).getId()
                );
                item.setItemMeta(meta);
                inventory.addItem(item);
            }
        }
    }

    public static String calTimeBetweenTwoDates(Date first, Date second) {
        long milliseconds = first.getTime() - second.getTime();
        int seconds = (int) (milliseconds / 1000);
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = (seconds % 3600) % 60;
        return hours + "h:" + minutes + "m:" + seconds + "s";
    }
}
