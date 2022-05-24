package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "cash_out_order")
@Cacheable
@ToString(includeFieldNames = true)
public class CashOutOrder {
    @Id
    @GeneratedValue
    private long id;
    private String text;
    private String orderNo;
    private long userId;
    private long amount;
    private long cardId;
    private int status;
    private String remark;
    private long addTime;
    private long updateTime;
}
