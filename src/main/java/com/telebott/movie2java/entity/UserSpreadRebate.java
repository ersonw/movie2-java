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
    @Id
    @GeneratedValue
    private long id;
    private long orderId;
    private long userId;
    private long amount;
    private int status;
    private long addTime;
    private long updateTime;
}
