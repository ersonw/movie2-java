package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface GameDao extends JpaRepository<Game, Long>, CrudRepository<Game, Long> {
    Game findAllByGameId(Integer integer);
    Game findAllById(long id);
    List<Game> findAllByStatus(int status);
}
