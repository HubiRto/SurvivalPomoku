package pl.pomoku.survivalpomoku.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Account {
    private int id;
    private String uuid;
    private double money;
}
