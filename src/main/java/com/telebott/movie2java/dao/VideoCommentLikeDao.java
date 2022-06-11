package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.VideoCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface VideoCommentLikeDao extends JpaRepository<VideoCommentLike, Long>, CrudRepository<VideoCommentLike, Long> {
    VideoCommentLike findAllById(long id);
    VideoCommentLike findAllByUserIdAndCommentId(long id, long commentId);
    long countAllByCommentId(long id);
    VideoCommentLike findAllByCommentId(long id);
    @Modifying
    @Query(value = "delete from `video_comment_like` WHERE comment_id=:id", nativeQuery = true)
    void removeAllByCommentId(long id);
}
