package pl.pomoku.survivalpomoku.enums;

import java.util.Map;

public enum SortType {
    NORMAL, OLDEST, LATEST, HIGHEST_PRICE, LOWEST_PRICE;
    private static int index = 0;
    public static final Map<SortType, String> names = Map.of(
            NORMAL, "<gold>↑↓ Losowo",
            OLDEST, "<red>↓ Od najstarszych",
            LATEST,"<green>↑ Od najnowszych",
            HIGHEST_PRICE, "<dark_red>↓ Od najdroższych",
            LOWEST_PRICE, "<dark_green>↑ Od najtańszych"
    );

    public static SortType next() {
        if (index + 1 == SortType.values().length) index = 0;
        return SortType.values()[++index];
    }
}
