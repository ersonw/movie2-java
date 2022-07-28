package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "game_out_card")
@Cacheable
@ToString(includeFieldNames = true)
public class GameOutCard {
    public GameOutCard() {}
    public GameOutCard(String name, String bank, String card, String address, String addIp) {
        this.name = name;
        this.bank = bank;
        this.card = card;
        this.address = address;
        this.addIp = addIp;
        this.addTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private String name;
    private String bank;
    private String card;
    private String address;
    private String addIp;
    private String updateIp;
    private long addTime;
    private long updateTime;
}
