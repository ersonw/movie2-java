package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.ShortVideoLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface ShortVideoLikeDao extends JpaRepository<ShortVideoLike, Long>, CrudRepository<ShortVideoLike, Long> {
    Long countAllByVideoId(long videoId);
    ShortVideoLike findAllByUserIdAndVideoId(long userId, long videoId);
}
