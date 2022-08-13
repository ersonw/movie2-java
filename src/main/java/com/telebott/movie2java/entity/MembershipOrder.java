package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "membership_order")
@Cacheable
@ToString(includeFieldNames = true)
public class MembershipOrder {
    @Id
    @GeneratedValue
    private long id;
    private String orderNo;
    private long userId;
    private long amount;
    private long price;
    private long gameCoin;
    private long experience;
    private long addTime;
}
