package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "video_comment_like")
@Cacheable
@ToString(includeFieldNames = true)
public class VideoCommentLike {
    public VideoCommentLike(){}
    public VideoCommentLike(long userId, long commentId,String ip){
        this.userId = userId;
        this.commentId = commentId;
        this.ip = ip;
        this.addTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long commentId;
    private String ip;
    private long addTime;
}
