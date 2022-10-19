package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.GameOutOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GameOutOrderDao extends JpaRepository<GameOutOrder, Long>, CrudRepository<GameOutOrder, Long> {
    GameOutOrder findAllById(long id);
    Page<GameOutOrder> findAllByUserId(long userId, Pageable pageable);
    @Query(value = "SELECT IFNULL( SUM(amount), 0 )  FROM `game_out_order` WHERE user_id=:userId AND status >= 0",nativeQuery = true)
    Long getAllBywBalance(long userId);
    @Query(value = "SELECT IFNULL( SUM(amount), 0 )  FROM `game_out_order` WHERE user_id=:userId AND status=:status",nativeQuery = true)
    Long getAllBywBalance(long userId, int status);
    @Query(value = "SELECT IFNULL( SUM(amount), 0 )  FROM `game_out_order` WHERE user_id=:userId AND status =0",nativeQuery = true)
    Long getAllByFreezeBalance(long userId);
    @Query(value = "SELECT (SELECT IFNULL( SUM(amount), 0 )  FROM game_order WHERE add_time < goo.add_time AND add_time > (SELECT MAX(add_time) FROM game_out_order WHERE status=1 AND add_time < goo.add_time AND user_id = goo.user_id LIMIT 1)) AS c FROM game_out_order AS goo  WHERE goo.id = (SELECT id FROM game_out_order WHERE (status = 0 OR status = 1) and user_id = :id ORDER BY add_time DESC LIMIT 1)", nativeQuery = true)
    Long getRecentIn(long id);
    @Query(value = " SELECT (SELECT IFNULL( SUM(valid_bet), 0 )  FROM game_water WHERE record_time < goo.add_time AND record_time > (SELECT MAX(add_time) FROM game_out_order WHERE status=1 AND add_time < goo.add_time AND user_id = goo.user_id  LIMIT 1)) AS c FROM `game_out_order` AS goo  WHERE goo.id = (SELECT id FROM game_out_order WHERE (status = 0 OR status = 1) and user_id = :id ORDER BY add_time DESC LIMIT 1)", nativeQuery = true)
    Long getRecentWater(long id);

}
