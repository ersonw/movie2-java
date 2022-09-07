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
    public UserConsume() {}
    public UserConsume(long userId, long amount, String text, int status) {
        this.userId = userId;
        this.amount = amount;
        this.text = text;
        this.status = status;
        this.addTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }
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
