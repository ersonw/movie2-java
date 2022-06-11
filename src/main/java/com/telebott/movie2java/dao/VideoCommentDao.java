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
    @Query(value = "SELECT vc.id,vc.reply_id,vc.user_id,vc.video_id,vc.video_time,vc.status,vc.text,vc.ip,vc.add_time, (SELECT COUNT(*) FROM `video_comment_like` WHERE comment_id = vc.id) AS c FROM `video_comment` vc WHERE `status`=:status ORDER BY c DESC", nativeQuery = true)
    Page<VideoComment> getAllByLike(int status, Pageable pageable);
    @Query(value = "SELECT vc.id,vc.reply_id,vc.user_id,vc.video_id,vc.video_time,vc.status,vc.text,vc.ip,vc.add_time, (SELECT COUNT(*) FROM `video_comment_like` WHERE comment_id = vc.id) AS c FROM `video_comment` vc WHERE `status`=:status AND reply_id=:replyId AND video_id=:videoId ORDER BY c DESC", nativeQuery = true)
    Page<VideoComment> getAllByLike(long replyId,long videoId,int status, Pageable pageable);

    VideoComment findAllByUserIdAndVideoIdAndText(long id, long id1, String text);
    @Modifying
    @Query(value = "DELETE vc.*,vcl.* FROM video_comment as vc LEFT JOIN video_comment_like as vcl ON vcl.comment_id=vc.id WHERE vc.reply_id =:id OR vc.id=:id", nativeQuery = true)
    void removeAllById(long id);
}
