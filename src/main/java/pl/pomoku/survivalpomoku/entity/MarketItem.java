package pl.pomoku.survivalpomoku.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketItem {
    private int id;
    private double price;
    private String uuid;
    private String player_name;
    private Date expiredDate;
    private ItemStack item;
}
