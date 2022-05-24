package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "live_profit")
@Cacheable
@ToString(includeFieldNames = true)
public class LiveProfit {
    @Id
    @GeneratedValue
    private long id;
    private long liveId;
    private long orderId;
    private long amount;
    private long addTime;
}
