package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "membership_experience")
@Cacheable
@ToString(includeFieldNames = true)
public class MembershipExperience {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private String text;
    private long experience;
    private long addTime;
    private long updateTime;
}
