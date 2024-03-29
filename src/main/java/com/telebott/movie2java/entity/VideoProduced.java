package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "video_produced")
@Cacheable
@ToString(includeFieldNames = true)
public class VideoProduced {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private int status;
    private long addTime;
    private long updateTime;
}
