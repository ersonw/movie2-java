package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "game_out_order")
@Cacheable
@ToString(includeFieldNames = true)
public class GameOutOrder {
    @Id
    @GeneratedValue
    private long id;
    private String orderNo;
    private long userId;
    private long amount;
    private long totalFee;
    private String name;
    private String bank;
    private String card;
    private String address;
    private int status;
    private String remark;
    private long addTime;
    private long updateTime;
}
