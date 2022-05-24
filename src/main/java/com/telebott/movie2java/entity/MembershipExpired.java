package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "membership_expired")
@Cacheable
@ToString(includeFieldNames = true)
public class MembershipExpired {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long expired;
    private long addTime;
    private long updateTime;
}
