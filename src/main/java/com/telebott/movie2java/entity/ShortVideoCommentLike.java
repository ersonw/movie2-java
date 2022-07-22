package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "short_video_comment_like")
@Cacheable
@ToString(includeFieldNames = true)
public class ShortVideoCommentLike {
    public ShortVideoCommentLike() {}
    public ShortVideoCommentLike(long userId,long commentId,String ip) {
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
    private long addTime;
    private String ip;
}
