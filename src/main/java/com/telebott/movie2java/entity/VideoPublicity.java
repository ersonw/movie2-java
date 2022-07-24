package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "video_publicity")
@Cacheable
@ToString(includeFieldNames = true)
public class VideoPublicity {
    @Transient
    public static final int OPEN_WEB_OUTSIDE = 0;
    @Transient
    public static final int OPEN_WEB_INSIDE = 1;
    @Transient
    public static final int OPEN_VIDEO = 2;
    @Transient
    public static final int OPEN_INLINE = 3;
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String pic;
    private String url;
    private long addTime;
    private int status;
    private int type;
}
