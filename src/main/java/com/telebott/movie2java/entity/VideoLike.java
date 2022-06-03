package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "video_like")
@Cacheable
@ToString(includeFieldNames = true)
public class VideoLike {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long videoId;
    private String ip;
    private long addTime;
}
