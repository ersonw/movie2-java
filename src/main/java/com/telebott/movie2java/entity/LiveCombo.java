package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "live_combo")
@Cacheable
@ToString(includeFieldNames = true)
public class LiveCombo {
    @Id
    @GeneratedValue
    private long id;
    private long price;
    private long countTime;
    private long liveId;
    private long addTime;
    private long updateTime;
}
