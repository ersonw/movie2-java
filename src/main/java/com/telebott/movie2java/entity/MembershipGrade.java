package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "membership_grade")
@Cacheable
@ToString(includeFieldNames = true)
public class MembershipGrade {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private long experience;
    private long addTime;
    private long updateTime;
}
