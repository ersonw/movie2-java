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
    public  AgentRebate(){}
    public  AgentRebate(long orderId, long agentId, double amount, int status){
        this.orderId = orderId;
        this.agentId = agentId;
        this.amount = amount;
        this.status = status;
        this.addTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private long id;
    private long orderId;
    private long agentId;
    private double amount;
    private int status;
    private long addTime;
    private long updateTime;
}
