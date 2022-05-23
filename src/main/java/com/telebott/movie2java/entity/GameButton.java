package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "game_button")
@Cacheable
@ToString(includeFieldNames = true)
public class GameButton {
    @Id
    @GeneratedValue
    private long id;
    private long amount;
    private double less;
    private long gift;
    private long giftType;
    private long addTime;
    private long updateTime;
}
