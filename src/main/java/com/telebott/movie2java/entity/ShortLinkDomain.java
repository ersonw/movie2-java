package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "short_link_domain")
@Cacheable
@ToString(includeFieldNames = true)
public class ShortLinkDomain {
    @Id
    @GeneratedValue
    private long id;
    private String hostname;
    private int status;
    private long addTime;
    private long updateTime;
}
