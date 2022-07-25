package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "cash_in_option")
@Cacheable
@ToString(includeFieldNames = true)
public class CashInOption {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String icon;
    private String code;
    private int status;
    private long addTime;
    private long updateTime;
}
