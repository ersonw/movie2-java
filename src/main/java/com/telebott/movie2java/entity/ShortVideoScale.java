package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "short_video_scale")
@Cacheable
@ToString(includeFieldNames = true)
public class ShortVideoScale {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long videoId;
    private long videoTime;
    private long addTime;
    private long updateTime;
}
