package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "short_link_record")
@Cacheable
@ToString(includeFieldNames = true)
public class ShortLinkRecord {
    public ShortLinkRecord() {
        this.addTime = System.currentTimeMillis();
    }
    public ShortLinkRecord(String linkId, String userAgent, String ip) {
        this.addTime = System.currentTimeMillis();
        this.linkId = linkId;
        this.userAgent = userAgent;
        this.ip = ip;
    }
    @Id
    @GeneratedValue
    private long id;
    private String linkId;
    private String userAgent;
    private String ip;
    private long addTime;
}
