package pl.pomoku.survivalpomoku.entity;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TimePlayer {
    private int id;
    private int accountId;
    private String playerUUID;
    private String playerName;
    private long totalTime;
    private long todayTime;
    private int collectedRewards;
    private boolean receivedAll;
}
