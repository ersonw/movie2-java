package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "game_water")
@Cacheable
@ToString(includeFieldNames = true)
public class GameWater {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long gameId;
    private long profit;
    private long balance;
    private long validBet;
    private long tax;
    private long recordTime;
    private String recordId;
    private String detailUrl;
    private long addTime;
}
