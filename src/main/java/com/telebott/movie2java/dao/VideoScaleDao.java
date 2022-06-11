package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.VideoScale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface VideoScaleDao extends JpaRepository<VideoScale, Long>, CrudRepository<VideoScale, Long> {
    VideoScale findAllById(long id);
    List<VideoScale> findAllByUserId(long id);
    VideoScale findAllByUserIdAndVideoId(long id, long videoId);
}
