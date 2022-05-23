package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "agent")
@Cacheable
@ToString(includeFieldNames = true)
public class Agent {
    @Id
    @GeneratedValue
    private long id;
    private String username;
    private String phone;
    private String email;
    private double fee;
    private double rebate;
    private double hide;
    private String salt;
    private String password;
    private long addTime;
    private long updateTime;
}
