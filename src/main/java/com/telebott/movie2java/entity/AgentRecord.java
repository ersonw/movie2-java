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
    public AgentRecord() {}
    public AgentRecord(long agentId, long userId, String ip) {
        this.agentId = agentId;
        this.userId = userId;
        this.ip = ip;
        addTime = System.currentTimeMillis();
        updateTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long agentId;
    private long userId;
    private long addTime;
    private long updateTime;
    private String ip;
}
