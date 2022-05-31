package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.CashInOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface CashInOrderDao extends JpaRepository<CashInOrder, Long>, CrudRepository<CashInOrder, Long> {
    CashInOrder findAllByOrderNo(String orderNo);
    CashInOrder findAllByOrderNoAndStatus(String orderNo, int status);
}
