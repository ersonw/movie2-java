package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "video_concentration_list")
@Cacheable
@ToString(includeFieldNames = true)
public class VideoConcentrationList {
    @Id
    @GeneratedValue
    private long id;
    private long concentrationId;
    private long videoId;
    private long addTime;
}
