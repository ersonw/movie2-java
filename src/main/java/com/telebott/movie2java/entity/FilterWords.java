package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "filter_words")
@Cacheable
@ToString(includeFieldNames = true)
public class FilterWords {
    @Id
    @GeneratedValue
    private long id;
    private String words;
    private int black;
    private long addTime;
    private long updateTime;
}
