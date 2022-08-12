package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "coin_order")
@Cacheable
@ToString(includeFieldNames = true)
public class CoinOrder {
    @Id
    @GeneratedValue
    private long id;
    private String orderNo;
    private long userId;
    private long amount;
    private long price;
    private long addTime;
}
