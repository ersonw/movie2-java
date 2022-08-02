package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user_follow")
@Cacheable
@ToString(includeFieldNames = true)
public class UserFollow {
    public UserFollow() {}
    public UserFollow(long userId, long toUserId, String ip) {
        this.userId = userId;
        this.toUserId = toUserId;
        this.ip = ip;
        this.addTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long toUserId;
    private int state;
    private String ip;
    private long addTime;
}
