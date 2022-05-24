package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user_device_record")
@Cacheable
@ToString(includeFieldNames = true)
public class UserDeviceRecord {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private String deviceId;
    private String platform;
    private long addTime;
}
