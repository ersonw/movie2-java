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
    @Id
    @GeneratedValue
    private long id;
    private long replyId;
    private long userId;
    private long videoId;
    private String text;
    private long addTime;
    private String ip;
}
