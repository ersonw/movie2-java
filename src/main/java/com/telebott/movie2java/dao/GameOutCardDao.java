package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.GameOutCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface GameOutCardDao extends JpaRepository<GameOutCard, Long>, CrudRepository<GameOutCard, Long> {
    GameOutCard findAllById(long id);
    GameOutCard findAllByUserIdAndCard(long userId, String card);
    Page<GameOutCard> findAllByUserId(long userId, Pageable pageable);
    List<GameOutCard> findAllByUserId(long userId);
}
