package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "video_produced_record")
@Cacheable
@ToString(includeFieldNames = true)
public class VideoProducedRecord {
    @Id
    @GeneratedValue
    private long id;
    private long producedId;
    private long videoId;
    private long addTime;
    private long updateTime;
}
