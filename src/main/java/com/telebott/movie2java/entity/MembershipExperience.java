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
    public MembershipExperience() {}
    public MembershipExperience(long userId, String text,long experience) {
        this.addTime = System.currentTimeMillis();
        this.userId = userId;
        this.text = text;
        this.experience = experience;
    }
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private String text;
    private long experience;
    private long addTime;
}
