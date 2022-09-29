package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface AppConfigDao extends JpaRepository<AppConfig, Long>, CrudRepository<AppConfig, Long> {
    @Query(value = "SELECT * FROM `app_config` ORDER BY add_time limit 1",nativeQuery = true)
    AppConfig getNewConfig();
}
