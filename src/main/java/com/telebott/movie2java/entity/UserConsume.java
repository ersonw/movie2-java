package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user_consume")
@Cacheable
@ToString(includeFieldNames = true)
public class UserConsume {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long amount;
    private String text;
    private int status;
    private long addTime;
    private long updateTime;
}
