package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "search_hot")
@Cacheable
@ToString(includeFieldNames = true)
public class SearchHot {
    @Id
    @GeneratedValue
    private long id;
    private String words;
    private String ip;
    private long userId;
    private long addTime;
}
