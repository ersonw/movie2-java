package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.LiveConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface LiveConfigDao extends JpaRepository<LiveConfig, Long>, CrudRepository<LiveConfig, Long> {
}
