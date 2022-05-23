package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "game")
@Cacheable
@ToString(includeFieldNames = true)
public class Game {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String image;
    private long gameId;
    private int status;
    private long addTime;
    private long updateTime;
}
