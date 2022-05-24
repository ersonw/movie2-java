package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "live_order_queue_count")
@Cacheable
@ToString(includeFieldNames = true)
public class LiveOrderQueueCount {
    @Id
    @GeneratedValue
    private long id;
    private long queueId;
    private long addTime;
}
