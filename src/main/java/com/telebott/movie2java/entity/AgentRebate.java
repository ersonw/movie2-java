package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "agent_rebate")
@Cacheable
@ToString(includeFieldNames = true)
public class AgentRebate {
    @Id
    @GeneratedValue
    private long id;
    private long orderId;
    private long agentId;
    private long amount;
    private int status;
    private long addTime;
    private long updateTime;
}
