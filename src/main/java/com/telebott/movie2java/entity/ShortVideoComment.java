package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "short_video_comment")
@Cacheable
@ToString(includeFieldNames = true)
public class ShortVideoComment {
    public ShortVideoComment(){}
    public ShortVideoComment(long replyId, long userId,long videoId, String text, String ip){
        this.replyId = replyId;
        this.userId = userId;
        this.videoId = videoId;
        this.text = text;
        this.ip = ip;
        this.addTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long replyId;
    private long userId;
    private long videoId;
    private String text;
    private int status=1;
    private int pin=0;
    private long addTime;
    private String ip;
}
