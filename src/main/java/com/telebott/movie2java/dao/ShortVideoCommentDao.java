package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.ShortVideoComment;
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
public interface ShortVideoCommentDao extends JpaRepository<ShortVideoComment, Long>, CrudRepository<ShortVideoComment, Long> {
    ShortVideoComment findAllById(long id);
    ShortVideoComment findAllByIdAndStatus(long id, int status);
    List<ShortVideoComment> findAllByVideoIdAndReplyId(long videoId, long replyId);
    Page<ShortVideoComment> findAllByReplyId(long replyId,Pageable pageable);
    Long countAllByReplyIdAndStatus(long replyId, int status);
    Long countAllByVideoIdAndStatus(long videoId, int status);
    @Query(value = "SELECT svc.*,(SELECT COUNT(*) FROM short_video_comment_like WHERE comment_id = svc.id) AS c FROM `short_video_comment` AS svc WHERE  svc.reply_id =0 and svc.status=1 ORDER BY c,svc.pin DESC",nativeQuery = true)
    Page<ShortVideoComment> getAllComments(Pageable pageable);
//    @Modifying
    @Query(value = "SELECT s.*,\n" +
            "(SELECT COUNT(*) FROM `short_video_comment_like` AS svcl WHERE svcl.comment_id = s.id) AS c \n" +
            "FROM \n" +
            "(SELECT svc.* \n" +
            " FROM `short_video_comment` AS svc \n" +
            " INNER JOIN `short_video_comment` AS svc1 ON svc1.reply_id =1 \n" +
            " INNER JOIN `short_video_comment` AS svc2 ON svc2.reply_id= svc1.id WHERE  svc.status=1) AS s \n" +
            " ORDER BY c,s.pin DESC",nativeQuery = true)
    Page<ShortVideoComment> getAllByReplyId(long replyId,Pageable pageable);
}
