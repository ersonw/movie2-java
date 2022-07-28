package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.GameOutConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface GameOutConfigDao extends JpaRepository<GameOutConfig, Long>, CrudRepository<GameOutConfig, Long> {
    GameOutConfig findAllById(long id);
    List<GameOutConfig> findAllByName(String name);
}
