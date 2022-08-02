package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.UserBalanceDiamond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserBalanceDiamondDao extends JpaRepository<UserBalanceDiamond, Long>, CrudRepository<UserBalanceDiamond, Long> {
    @Query(value = "SELECT IFNULL( SUM(amount), 0 )  FROM `user_balance_diamond` WHERE user_id=:userId",nativeQuery = true)
    Long getAllByBalance(long userId);
}
