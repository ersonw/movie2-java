package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "video")
@Cacheable
@ToString(includeFieldNames = true)
public class Video {
    @Id
    @GeneratedValue
    private long id;
    private String title;
    private String picThumb;
    private long vodClass;
    private String shareId;
    private long vodDuration;
    private String vodPlayUrl;
    private String vodContent;
    private int status;
    private long plays;
    private long likes;
    private long trial;
    private long addTime;
    private long updateTime;
}
