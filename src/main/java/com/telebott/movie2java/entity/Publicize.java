package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "publicize")
@Cacheable
@ToString(includeFieldNames = true)
public class Publicize {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String image;
    private String url;
    private int type;
    private int page;
    private int status;
    private long addTime;
    private long updateTime;
}
