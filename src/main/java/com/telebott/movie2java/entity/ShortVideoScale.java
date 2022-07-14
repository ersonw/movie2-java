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
    public ShortVideoScale(){}
    public ShortVideoScale(long userId,long videoId, long videoTime,String ip){
        this.userId = userId;
        this.videoId = videoId;
        this.videoTime = videoTime;
        this.ip = ip;
        this.addTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long videoId;
    private long videoTime;
    private long addTime;
    private long updateTime;
    private String ip;
}
