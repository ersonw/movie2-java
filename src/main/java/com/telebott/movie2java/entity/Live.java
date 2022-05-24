package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "live")
@Cacheable
@ToString(includeFieldNames = true)
public class Live {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private String name;
    private String text;
    private String location;
    private long addTime;
    private long updateTime;
}
