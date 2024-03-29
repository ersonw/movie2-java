package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "cash_in_order")
@Cacheable
@ToString(includeFieldNames = true)
public class CashInOrder {
    @Id
    @GeneratedValue
    private long id;
    private String orderId;
    private String orderNo;
    private int orderType;
    private Long type;
    private String tradeNo;
    private String totalFee;
    private int status;
    private long addTime;
    private long updateTime;
    private String ip;
}
