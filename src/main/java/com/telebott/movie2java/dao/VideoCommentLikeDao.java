package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.VideoCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface VideoCommentLikeDao extends JpaRepository<VideoCommentLike, Long>, CrudRepository<VideoCommentLike, Long> {
    long countAllByCommentId(long id);
}
