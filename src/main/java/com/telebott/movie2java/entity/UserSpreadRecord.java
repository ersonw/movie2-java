package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user_spread_record")
@Cacheable
@ToString(includeFieldNames = true)
public class UserSpreadRecord {
    public UserSpreadRecord() {}
    public UserSpreadRecord(long userId, long shareUserId, String ip) {
        this.userId = userId;
        this.shareUserId = shareUserId;
        this.ip = ip;
        addTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long shareUserId;
    private long userId;
    private String ip;
    private long addTime;
}
