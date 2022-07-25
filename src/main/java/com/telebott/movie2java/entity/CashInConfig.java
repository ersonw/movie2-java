package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "cash_in_config")
@Cacheable
@ToString(includeFieldNames = true)
public class CashInConfig {
    @Id
    @GeneratedValue
    private long id;
    private String title;
    private String domain;
    private String mchId;
    private String secretKey;
    private String callbackUrl;
    private String notifyUrl;
    private String errorUrl;
    private String allowed;
    private int status;
    private long addTime;
    private long updateTime;
}
