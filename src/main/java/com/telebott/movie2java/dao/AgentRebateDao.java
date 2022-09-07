package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.AgentRebate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface AgentRebateDao extends JpaRepository<AgentRebate, Long>, CrudRepository<AgentRebate, Long> {
    long countAllByAgentId(long userId);

    long countAllByAgentIdAndAddTimeGreaterThanEqual(long userId, long todayZero);

    long countAllByAgentIdAndStatusAndAddTimeGreaterThanEqual(long userId, int i, long todayZero);
}
