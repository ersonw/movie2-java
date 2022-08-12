package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.UserBalanceCoin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserBalanceCoinDao extends JpaRepository<UserBalanceCoin, Long>, CrudRepository<UserBalanceCoin, Long> {
    @Query(value = "SELECT IFNULL( SUM(amount), 0 )  FROM `user_balance_coin` WHERE user_id=:userId",nativeQuery = true)
    Long getAllByBalance(long userId);

    Page<UserBalanceCoin> findAllByUserId(long id, Pageable pageable);
}
