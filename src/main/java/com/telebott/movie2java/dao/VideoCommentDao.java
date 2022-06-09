package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.VideoComment;
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
public interface VideoCommentDao extends JpaRepository<VideoComment, Long>, CrudRepository<VideoComment, Long> {
    VideoComment findAllById(long id);
    List<VideoComment> findAllByReplyId(long replyId);
    List<VideoComment> findAllByReplyIdAndVideoIdAndUserIdAndStatus(long replyId, long videoId, long userId, int status);
    long countAllByReplyIdAndVideoIdAndStatus(long replyId, long videoId, int status);
    @Query(value = "SELECT vc.id, (SELECT COUNT(*) FROM `video_comment_like` WHERE comment_id = vc.id) AS c FROM `video_comment` vc WHERE `status`=:status ORDER BY c DESC", nativeQuery = true)
    Page<VideoComment> getAllByLike(int status, Pageable pageable);

    VideoComment findAllByUserIdAndVideoIdAndText(long id, long id1, String text);
    @Modifying
    @Query(value = "delete from `video_comment` WHERE video_id=:id", nativeQuery = true)
    void removeAllByVideoId(long id);
    @Modifying
    @Query(value = "delete from `video_comment` WHERE reply_id=:id", nativeQuery = true)
    void removeAllByToId(long id);
    @Modifying
    @Query(value = "delete from `video_comment` WHERE user_id=:id", nativeQuery = true)
    void removeAllByUserId(long id);
}
