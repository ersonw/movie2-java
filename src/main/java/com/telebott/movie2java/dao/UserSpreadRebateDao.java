package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.UserSpreadRebate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserSpreadRebateDao extends JpaRepository<UserSpreadRebate, Long>, CrudRepository<UserSpreadRebate, Long> {
    @Query(value = "SELECT IFNULL( SUM(amount), 0 )  FROM `user_spread_rebate` WHERE user_id=:userId",nativeQuery = true)
    Double getAllByBalance(long userId);
}
