package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "live_order_queue")
@Cacheable
@ToString(includeFieldNames = true)
public class LiveOrderQueue {
    @Id
    @GeneratedValue
    private long id;
    private long orderId;
    private int status;
    private long addTime;
    private long updateTime;
}
