package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user_balance_cash")
@Cacheable
@ToString(includeFieldNames = true)
public class UserBalanceCash {
    public UserBalanceCash() {}
    public UserBalanceCash(long userId, double amount, String text) {
        this.userId = userId;
        this.amount = amount;
        this.text = text;
        this.addTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private double amount;
    private String text;
    private long addTime;
}
