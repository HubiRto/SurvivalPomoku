package pl.pomoku.survivalpomoku.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopItem {
    private int id;
    private double price;
    private String uuid;
    private Date expiredDate;
    private String item;
}
