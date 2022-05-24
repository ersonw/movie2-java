package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user_balance_diamond")
@Cacheable
@ToString(includeFieldNames = true)
public class UserBalanceDiamond {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long amount;
    private String text;
    private long addTime;
}
