package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "app_config")
@Cacheable
@ToString(includeFieldNames = true)
public class AppConfig {
    @Id
    @GeneratedValue
    private long id;
    private String mainUrl;
    private String mainDomain;
    private String channelDomain;
    private String splashList;
    private String version;
    private String download;
    private long addTime;
    private long updateTime;
}
