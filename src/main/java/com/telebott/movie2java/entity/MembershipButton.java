package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "membership_button")
@Cacheable
@ToString(includeFieldNames = true)
public class MembershipButton {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private long amount;
    private double less;
    private long experience;
    private long addTime;
    private long updateTime;
}
