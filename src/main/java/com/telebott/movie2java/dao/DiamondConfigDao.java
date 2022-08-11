package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.DiamondConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DiamondConfigDao extends JpaRepository<DiamondConfig, Long>, CrudRepository<DiamondConfig, Long> {
    DiamondConfig findAllById(long id);
    List<DiamondConfig> findAllByName(String name);
}
