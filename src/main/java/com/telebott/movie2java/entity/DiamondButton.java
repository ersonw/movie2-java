package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "diamond_button")
@Cacheable
public class DiamondButton {
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
