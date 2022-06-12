package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.VideoProducedRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface VideoProducedRecordDao extends JpaRepository<VideoProducedRecord, Long>, CrudRepository<VideoProducedRecord, Long> {
//    @Modifying
//    @Query(value = "SELECT vc.* FROM (SELECT * FROM `video` WHERE status=:status AND vod_class > 0 GROUP BY `vod_class`) v LEFT JOIN `video_class` vc ON v.vod_class=vc.id ", nativeQuery = true)
//    List<VideoClass> findAllByStatus(int status);
}
