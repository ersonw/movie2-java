package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "live_concentration_list")
@Cacheable
@ToString(includeFieldNames = true)
public class LiveConcentrationList {
    @Id
    @GeneratedValue
    private long id;
    private long liveId;
    private long concentrationId;
    private long addTime;
    private long updateTime;
}
