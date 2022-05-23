package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "agent_record")
@Cacheable
@ToString(includeFieldNames = true)
public class AgentRecord {
    @Id
    @GeneratedValue
    private long id;
    private long agentId;
    private long userId;
    private long addTime;
    private long updateTime;
}
