package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "agent_channel")
@Cacheable
@ToString(includeFieldNames = true)
public class AgentChannel {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private long agentId;
    private String channel;
    private long addTime;
    private long updateTime;
}
