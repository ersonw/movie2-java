package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.CashConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
@Repository
public interface CashConfigDao extends JpaRepository<CashConfig, Long>, CrudRepository<CashConfig, Long> {
    CashConfig findAllById(long id);
    List<CashConfig> findAllByName(String name);
}
