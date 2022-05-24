package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "live_concentration")
@Cacheable
@ToString(includeFieldNames = true)
public class LiveConcentration {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private long addTime;
    private long updateTime;
}
