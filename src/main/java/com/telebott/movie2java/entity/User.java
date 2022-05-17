package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user")
@Cacheable
@ToString(includeFieldNames = true)
public class User {
    @Id
    @GeneratedValue
    private long id;
    private String uid;
    private String identifier;
    @Transient
    private String token;
}
