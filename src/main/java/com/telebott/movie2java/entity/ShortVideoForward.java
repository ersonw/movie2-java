package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "short_video_forward")
@Cacheable
@ToString(includeFieldNames = true)
public class ShortVideoForward {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long videoId;
    private long correctVideoId;
    private String ip;
    private long addTime;
    private long updateTime;
}
