package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.CashInConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface CashInConfigDao extends JpaRepository<CashInConfig, Long>, CrudRepository<CashInConfig, Long> {
    List<CashInConfig> findAllByMchIdAndStatus(String mchId,int status);
    List<CashInConfig> findAllByMchId(String mchId);
    CashInConfig findAllById(Long mchId);
    List<CashInConfig> findAllByStatus(int status);
}
