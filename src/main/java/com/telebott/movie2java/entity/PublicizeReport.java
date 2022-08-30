package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "publicize_report")
@Cacheable
@ToString(includeFieldNames = true)
public class PublicizeReport {
    public PublicizeReport() {}
    public PublicizeReport(long publicityId, long userId, String ip) {
        this.publicityId = publicityId;
        this.userId = userId;
        this.ip = ip;
        this.addTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long publicityId;
    private long userId;
    private String ip;
    private long addTime;
}
