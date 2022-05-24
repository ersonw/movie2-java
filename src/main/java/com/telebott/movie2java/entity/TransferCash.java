package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "transfer_cash")
@Cacheable
@ToString(includeFieldNames = true)
public class TransferCash {
    @Id
    @GeneratedValue
    private long id;
    private long amount;
    private long userId;
    private long toUserId;
    private long addTime;
}
