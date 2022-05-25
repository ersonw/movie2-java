package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "video_pay")
@Cacheable
@ToString(includeFieldNames = true)
public class VideoPay {
    @Id
    @GeneratedValue
    private long id;
    private long amount;
    private long videoId;
    private long addTime;
    private long updateTime;
}
