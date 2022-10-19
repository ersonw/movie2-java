package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "video_concentration")
@Cacheable
@ToString(includeFieldNames = true)
public class VideoConcentration {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private int px;
    private long addTime;
    private long updateTime;
}
