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
    private long amount;
    private long price;
    private int less;
    private long cashInId;
    private int status;
    private long addTime;
    private long updateTime;
}
