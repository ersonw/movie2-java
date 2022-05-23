package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "game_carousel")
@Cacheable
@ToString(includeFieldNames = true)
public class GameCarousel {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String pic;
    private String jump;
    private long jumpType;
    private int status;
    private long addTime;
    private long updateTime;
}
