package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "video_scale")
@Cacheable
@ToString(includeFieldNames = true)
public class VideoScale {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long videoId;
    private long videoTime;
    private long addTime;
    private long updateTime;
}
