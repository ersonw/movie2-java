package com.telebott.movie2java.data;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class wRecord {
    private List<String> uid;
    private List<Integer> game;
    private List<String> profit;
    private List<String> balance;
    private List<String> validBet;
    private List<String> tax;
    private List<String> recordTime;
    private List<String> recordId;
    private List<String> detailUrl;
}
