package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "agent_balance_cash")
@Cacheable
@ToString(includeFieldNames = true)
public class AgentBalanceCash {
    @Id
    @GeneratedValue
    private long id;
    private long agentId;
    private long amount;
    private long addTime;
    private long updateTime;
}
