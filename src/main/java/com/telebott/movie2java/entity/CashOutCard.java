package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "cash_out_card")
@Cacheable
@ToString(includeFieldNames = true)
public class CashOutCard {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private String name;
    private String bank;
    private String card;
    private String address;
    private long addTime;
    private long updateTime;
}
