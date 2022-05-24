package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user_share_code")
@Cacheable
@ToString(includeFieldNames = true)
public class UserShareCode {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private long userId;
    private String inviteCode;
    private long addTime;
}
