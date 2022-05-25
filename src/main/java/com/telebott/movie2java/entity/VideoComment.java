package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "video_play")
@Cacheable
@ToString(includeFieldNames = true)
public class VideoComment {
    @Id
    @GeneratedValue
    private long id;
    private long replyId;
    private long userId;
    private long videoId;
    private long videoTime;
    private String text;
    private long addTime;
}
