package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.AgentConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface AgentConfigDao extends JpaRepository<AgentConfig, Long>, CrudRepository<AgentConfig, Long> {
    List<AgentConfig> findAllByName(String name);
}
