package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.VideoPublicityReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface VideoPublicityReportDao extends JpaRepository<VideoPublicityReport, Long>, CrudRepository<VideoPublicityReport, Long> {
}
