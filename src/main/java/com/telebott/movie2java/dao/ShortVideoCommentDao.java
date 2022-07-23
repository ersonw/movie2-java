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
    @Query(value = "SELECT COUNT(*) FROM (SELECT svc.* FROM `short_video_comment` AS svc\n" +
            "LEFT JOIN `short_video_comment` AS svc1 ON svc1.reply_id=:id\n" +
            "LEFT JOIN `short_video_comment` AS svc2 ON svc2.reply_id=svc1.id\n" +
            "WHERE (svc.id =svc1.id OR svc.id = svc2.id) AND svc.status=1 ) AS s1",nativeQuery = true)
    Long countAllByReply(long id);
    @Query(value = "SELECT * FROM (\n" +
            "SELECT svc.*,(SELECT COUNT(*) FROM short_video_comment_like WHERE comment_id = svc.id) AS c \n" +
            "            FROM `short_video_comment` AS svc\n" +
            "            WHERE  svc.reply_id =0 and svc.status=1\n" +
            "            ORDER BY c DESC\n" +
            ") as s ORDER BY s.pin DESC",nativeQuery = true)
    Page<ShortVideoComment> getAllComments(Pageable pageable);

    @Query(value = "SELECT * FROM(\n" +
            "SELECT svc.*,(SELECT COUNT(*) FROM `short_video_comment_like` AS svcl WHERE svcl.comment_id = svc.id) AS c\n" +
            "            FROM `short_video_comment` AS svc\n" +
            "            LEFT JOIN `short_video_comment` AS svc1 ON svc1.reply_id=:replyId\n" +
            "            LEFT JOIN `short_video_comment` AS svc2 ON svc2.reply_id=svc1.id\n" +
            "            WHERE (svc.id =svc1.id OR svc.id = svc2.id) AND svc.status=1\n" +
            "            ORDER BY c DESC\n" +
            ") as s ORDER BY s.pin DESC",nativeQuery = true)
    Page<ShortVideoComment> getAllByReplyId(long replyId,Pageable pageable);

    @Query(value = "DELETE FROM `short_video_comment` WHERE id IN (\n" +
            "    SELECT s1.id FROM(\n" +
            "        SELECT svc.* FROM `short_video_comment` AS svc\n" +
            "        LEFT JOIN `short_video_comment` AS svc1 ON svc1.reply_id=:id\n" +
            "        LEFT JOIN `short_video_comment` AS svc2 ON svc2.reply_id=svc1.id\n" +
            "        WHERE (svc.id = :id OR svc.id =svc1.id OR svc.id = svc2.id) AND svc.status=1\n" +
            "    ) AS s1\n" +
            ")",nativeQuery = true)
    @Modifying
    void deleteAllByComment(long id);
    @Query(value = "DELETE FROM `short_video_comment_like` WHERE comment_id IN (\n" +
            "    SELECT s1.id FROM(\n" +
            "        SELECT svc.* FROM `short_video_comment` AS svc\n" +
            "        LEFT JOIN `short_video_comment` AS svc1 ON svc1.reply_id=10\n" +
            "        LEFT JOIN `short_video_comment` AS svc2 ON svc2.reply_id=svc1.id\n" +
            "        WHERE (svc.id = 10 OR svc.id =svc1.id OR svc.id = svc2.id) AND svc.status=1\n" +
            "    ) AS s1\n" +
            ")",nativeQuery = true)
    @Modifying
    void deleteAllByLike(long id);
    @Query(value = "DELETE FROM `short_video_comment_report` WHERE comment_id IN (\n" +
            "    SELECT s1.id FROM(\n" +
            "        SELECT svc.* FROM `short_video_comment` AS svc\n" +
            "        LEFT JOIN `short_video_comment` AS svc1 ON svc1.reply_id=10\n" +
            "        LEFT JOIN `short_video_comment` AS svc2 ON svc2.reply_id=svc1.id\n" +
            "        WHERE (svc.id = 10 OR svc.id =svc1.id OR svc.id = svc2.id) AND svc.status=1\n" +
            "    ) AS s1\n" +
            ")",nativeQuery = true)
    @Modifying
    void deleteAllByReport(long id);
}
