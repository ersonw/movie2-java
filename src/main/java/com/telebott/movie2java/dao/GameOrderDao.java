package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.GameOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface GameOrderDao extends JpaRepository<GameOrder, Long>, CrudRepository<GameOrder, Long> {
    GameOrder findAllByOrderNo(String orderNo);
    GameOrder findAllById(long id);
}
