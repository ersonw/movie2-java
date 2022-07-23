package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.ShortVideoCommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface ShortVideoCommentReportDao extends JpaRepository<ShortVideoCommentReport, Long>, CrudRepository<ShortVideoCommentReport, Long> {
    Long countAllByCommentIdAndState(long commentId, int state);
}
