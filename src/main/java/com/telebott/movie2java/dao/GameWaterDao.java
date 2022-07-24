package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.GameWater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface GameWaterDao extends JpaRepository<GameWater, Long>, CrudRepository<GameWater, Long> {
    GameWater findAllByRecordId(String s);
    @Query(value = "SELECT t1.*\n" +
            "FROM game_water t1\n" +
            "INNER JOIN (\n" +
            "\tSELECT MAX(id) AS id,game_id\n" +
            "\tFROM game_water t1\n" +
            "    WHERE user_id=:userId\n" +
            "\tGROUP BY game_id\n" +
            "\t) t2\n" +
            "ON t2.game_id = t1.game_id\n" +
            "AND t2.id = t1.id order by t1.id desc\n",nativeQuery = true)
    List<GameWater> getAllByUser(long userId);
}
