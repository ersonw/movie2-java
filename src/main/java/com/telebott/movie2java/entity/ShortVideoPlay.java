package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "short_video_play")
@Cacheable
@ToString(includeFieldNames = true)
public class ShortVideoPlay {
    public ShortVideoPlay() {}
    public ShortVideoPlay(long videoId, long userId, String ip) {
        this.videoId = videoId;
        this.userId = userId;
        this.ip = ip;
        this.addTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long videoId;
    private String ip;
    private long addTime;
}
