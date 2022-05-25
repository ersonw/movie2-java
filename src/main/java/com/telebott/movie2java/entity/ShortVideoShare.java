package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "short_video_share")
@Cacheable
@ToString(includeFieldNames = true)
public class ShortVideoShare {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long toUserId;
    private long videoId;
    private long addTime;
}
