package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "sms_config")
@Cacheable
@ToString(includeFieldNames = true)
public class ShortVideoCommentLike {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long commentId;
    private long addTime;
    private String ip;
}
