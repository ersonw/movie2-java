package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user_balance_diamond")
@Cacheable
@ToString(includeFieldNames = true)
public class UserBalanceDiamond {
    public UserBalanceDiamond(){}
    public UserBalanceDiamond(long userId,long amount, String text){
        this.userId = userId;
        this.amount = amount;
        this.text = text;
        this.addTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long amount;
    private String text;
    private long addTime;
}
