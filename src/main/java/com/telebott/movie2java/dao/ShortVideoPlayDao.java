package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.ShortVideoPlay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ShortVideoPlayDao extends JpaRepository<ShortVideoPlay, Long>, CrudRepository<ShortVideoPlay, Long> {
    List<ShortVideoPlay> findAllByVideoIdAndUserIdAndAddTimeGreaterThanEqual(Long videoId, Long userId, long time);

    Long countAllByVideoId(long id);
}
