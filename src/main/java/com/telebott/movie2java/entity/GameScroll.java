package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "game_scroll")
@Cacheable
@ToString(includeFieldNames = true)
public class GameScroll {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private long amount;
    private String game;
    private long addTime;
}
