package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "live_verified")
@Cacheable
@ToString(includeFieldNames = true)
public class LiveVerified {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private String name;
    private String number;
    private int status;
    private long addTime;
    private long updateTime;
}
