package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user_spread_rebate")
@Cacheable
@ToString(includeFieldNames = true)
public class UserSpreadRebate {
    public UserSpreadRebate() {}
    public UserSpreadRebate(long orderId, long userId, double amount, int status) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.addTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long orderId;
    private long userId;
    private double amount;
    private int status;
    private long addTime;
    private long updateTime;
}
