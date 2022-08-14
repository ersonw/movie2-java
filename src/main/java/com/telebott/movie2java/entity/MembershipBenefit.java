package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "membership_benefit")
@Cacheable
@ToString(includeFieldNames = true)
public class MembershipBenefit {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String icon;
    private long addTime;
    private long updateTime;
}
