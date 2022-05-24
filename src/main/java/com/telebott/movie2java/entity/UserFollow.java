package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user_follow")
@Cacheable
@ToString(includeFieldNames = true)
public class UserFollow {
    @Id
    @GeneratedValue
    private long id;
    private long userId;
    private long toUserId;
    private long addTime;
}
