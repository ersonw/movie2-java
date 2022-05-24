package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "live_order")
@Cacheable
@ToString(includeFieldNames = true)
public class LiveOrder {
    @Id
    @GeneratedValue
    private long id;
    private long liveId;
    private long userId;
    private long amount;
    private long countTime;
    private int status;
    private long addTime;
    private long updateTime;
}
