package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user_spread_config")
@Cacheable
@ToString(includeFieldNames = true)
public class UserSpreadConfig {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String val;
    private long addTime;
    private long updateTime;
}
