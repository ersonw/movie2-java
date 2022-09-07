package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "game_funds")
@Cacheable
@ToString(includeFieldNames = true)
public class GameFunds {
    public GameFunds(){}
    public GameFunds(long userId,long amount,String text, long addTime){
        this.userId = userId;
        this.amount = amount;
        this.text = text;
        this.addTime= addTime;
    }
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long amount;
    private String text;
    private long addTime;
}
