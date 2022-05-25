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
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long commentId;
    private long addTime;
}
