package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.VideoConcentration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface VideoConcentrationDao extends JpaRepository<VideoConcentration, Long>, CrudRepository<VideoConcentration, Long> {
    VideoConcentration findAllById(long id);
    @Query(value = "SELECT * FROM video_concentration WHERE id IN (   SELECT s.id FROM (SELECT *,(SELECT COUNT(*) FROM `video_concentration_list` AS vcl INNER JOIN video AS v on v.id=vcl.video_id AND v.status =1 WHERE vcl.concentration_id=vc.id) AS c FROM `video_concentration` AS vc ORDER BY c DESC) AS s WHERE s.c > 0)",nativeQuery = true)
    List<VideoConcentration> findAllByList();
    @Query(value = "SELECT * FROM video_concentration WHERE id IN (   SELECT s.id FROM (SELECT *,(SELECT COUNT(*) FROM `video_concentration_list` AS vcl INNER JOIN video AS v on v.id=vcl.video_id AND v.status =1 WHERE vcl.concentration_id=vc.id) AS c FROM `video_concentration` AS vc ) AS s WHERE s.c > 0)",nativeQuery = true)
    Page<VideoConcentration> findAllByList(Pageable pageable);
}
