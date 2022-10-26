package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user_robot")
@Cacheable
@ToString(includeFieldNames = true)
public class UserRobot {
    public UserRobot() {
        this.addTime=System.currentTimeMillis();
        this.updateTime=System.currentTimeMillis();
    }
    public UserRobot(long userId) {
        this.userId = userId;
        this.addTime=System.currentTimeMillis();
        this.updateTime=System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long addTime;
    private long updateTime;
}
