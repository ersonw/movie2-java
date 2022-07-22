package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "short_video")
@Cacheable
@ToString(includeFieldNames = true)
public class ShortVideo {
    @Id
    @GeneratedValue
    private long id;
    private long formId;
    private String title;
    private long userId;
    private String pic;
    private String playUrl;
    private long duration;
    private String file;
    private String ip;
    private int forward;
    private int status;
    private long addTime;
    private long updateTime;
}
