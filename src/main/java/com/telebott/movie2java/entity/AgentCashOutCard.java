package com.telebott.movie2java.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "agent_cash_out_card")
@Cacheable
@ToString(includeFieldNames = true)
public class AgentCashOutCard {
    @Id
    @GeneratedValue
    private long id;
    private long agentId;
    private String name;
    private String bank;
    private String card;
    private String address;
    private long addTime;
    private long updateTime;
}
