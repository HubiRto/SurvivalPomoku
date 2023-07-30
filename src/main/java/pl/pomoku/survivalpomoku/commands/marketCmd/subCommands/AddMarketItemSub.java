package pl.pomoku.survivalpomoku.commands.marketCmd.subCommands;

import lombok.SneakyThrows;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.pomoku.survivalpomoku.commandManagerLib.SubCommand;
import pl.pomoku.survivalpomoku.entity.MarketItem;

import java.sql.Date;
import java.util.List;

import static org.bukkit.Material.AIR;
import static pl.pomoku.pomokupluginsrepository.text.Text.strToComp;
import static pl.pomoku.survivalpomoku.SurvivalPomoku.plugin;

public class  AddMarketItemSub implements SubCommand {
    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "Wystawia twój przedmiot na rynek";
    }

    @Override
    public String getSyntax() {
        return "/market add <price>";
    }

    @Override
    public String getPermission() {
        return "market.add";
    }

    @Override
    public List<String> getTabCompletion(int index, String[] args) {
        return null;
    }

    @SneakyThrows
    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(strToComp("<red>Nie możesz wykonać tej komendy z konsoli!"));
            return;
        }

        double price;

        try {
            price = Double.parseDouble(args[0]);
        } catch (Exception exception) {
            sender.sendMessage(strToComp("<red>Użycie: <gray>" + getSyntax()));
            return;
        }

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType() == AIR) {
            player.sendMessage(strToComp("<red>Brak przedmiotu w ręku."));
            return;
        }

        plugin.getMarketItemDAO().insert(MarketItem.builder()
                        .item(itemInMainHand)
                        .uuid(player.getUniqueId().toString())
                        .player_name(player.getName())
                        .expiredDate(new Date(System.currentTimeMillis()))
                        .price(price)
                .build());
        player.getInventory().removeItem(player.getInventory().getItemInMainHand());
        player.sendMessage(strToComp("<gray>Wystawiłeś przedmiot na sprzedaż za <green>" + price + "<bold>$"));
    }
}
