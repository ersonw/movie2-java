package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.ShortVideoComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ShortVideoCommentDao extends JpaRepository<ShortVideoComment, Long>, CrudRepository<ShortVideoComment, Long> {
    List<ShortVideoComment> findAllByVideoIdAndReplyId(long videoId, long replyId);
    Long countAllByVideoId(long videoId);
}
