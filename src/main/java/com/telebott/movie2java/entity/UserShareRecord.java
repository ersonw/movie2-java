package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user_share_record")
@Cacheable
@ToString(includeFieldNames = true)
public class UserShareRecord {
    public UserShareRecord() {}
    public UserShareRecord(String type, String ip, long userId) {
        this.type = type;
        this.ip = ip;
        this.userId = userId;
        this.addTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private String type;
    private String ip;
    private long userId;
    private long addTime;
}
