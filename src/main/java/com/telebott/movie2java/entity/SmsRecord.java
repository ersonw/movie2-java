package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "sms_record")
@ToString(includeFieldNames = true)
@Setter
@Getter
public class SmsRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String code;
    private String phone;
    private String data;
    private String ip;
    private int status;
    private long addTime;
    private long updateTime;
}
