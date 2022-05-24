package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "diamond_order")
@Cacheable
@ToString(includeFieldNames = true)
public class DiamondOrder {
    @Id
    @GeneratedValue
    private long id;
    private String orderNo;
    private long userId;
    private long amount;
    private long buttonId;
    private int status;
    private long addTime;
    private long updateTime;
}
