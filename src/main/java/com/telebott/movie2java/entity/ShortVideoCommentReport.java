package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "short_video_comment_report")
@Cacheable
@ToString(includeFieldNames = true)
public class ShortVideoCommentReport {
    public ShortVideoCommentReport() {}
    public ShortVideoCommentReport(long commentId,long userId, String ip) {
        this.commentId = commentId;
        this.userId = userId;
        this.ip = ip;
        this.state =0;
        this.addTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long commentId;
    private long userId;
    private String ip;
    private int state;
    private long addTime;
}
