package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface VideoDao extends JpaRepository<Video, Long>, CrudRepository<Video, Long> {
    Video findAllByShareId(String shareId);
    Video findAllById(long id);
    Page<Video> findAllByTitle(String title, Pageable pageable);
    Page<Video> findAllByTitleLikeAndStatus(String title,int status, Pageable pageable);

    //无跟条件
    Page<Video> findAllByStatus(int status, Pageable pageable);
    //单独vodClass条件
    Page<Video> findAllByVodClassAndStatus(long vodClass,int status, Pageable pageable);
    //单独produced条件
    @Query(value = "SELECT v.* FROM `video_produced_record` AS vpr INNER JOIN `video` AS v ON vpr.video_id=v.id AND v.status=1 WHERE vpr.produced_id=:id", nativeQuery = true)
    Page<Video> getAllByProduced(long id,Pageable pageable);
    //vodClass produced 并发条件
    @Query(value = "SELECT v.* FROM `video_produced_record` AS vpr INNER JOIN `video` AS v ON vpr.video_id=v.id AND v.status=1 AND v.vod_class=:vodClass WHERE vpr.produced_id=:producedId", nativeQuery = true)
    Page<Video> getAllByVodClassAndProduced(long vodClass,long producedId,Pageable pageable);


    //vodClass produced 并发条件
    @Query(value = "SELECT v.*,(v.plays+(SELECT COUNT(*) FROM `video_play` WHERE video_id= v.id)) AS c FROM `video` v WHERE  v.status=:status ORDER BY c DESC", nativeQuery = true)
    Page<Video> getVideoByStatus(int status,Pageable pageable);
    //vodClass produced 并发条件
    @Query(value = "SELECT v.*,(v.plays+(SELECT COUNT(*) FROM `video_play` WHERE video_id= v.id)) AS c FROM `video` v WHERE v.vod_class=:vodClass AND v.status=1 ORDER BY c DESC", nativeQuery = true)
    Page<Video> getVideoByVodClass(long vodClass,Pageable pageable);
    //vodClass produced 并发条件
    @Query(value = "SELECT v.*,(v.plays+(SELECT COUNT(*) FROM `video_play` WHERE video_id= v.id)) AS c FROM `video_produced_record` AS vpr INNER JOIN `video` AS v ON vpr.video_id=v.id AND v.status=1 WHERE vpr.produced_id=:producedId ORDER BY c DESC", nativeQuery = true)
    Page<Video> getVideoByProduced(long producedId,Pageable pageable);
    //vodClass produced 并发条件
    @Query(value = "SELECT v.*,(v.plays+(SELECT COUNT(*) FROM `video_play` WHERE video_id= v.id)) AS c  FROM `video_produced_record` AS vpr INNER JOIN `video` AS v ON vpr.video_id=v.id AND v.status=1 AND v.vod_class=:vodClass WHERE vpr.produced_id=:producedId ORDER BY c DESC", nativeQuery = true)
    Page<Video> getVideoByVodClassAndProduced(long vodClass,long producedId,Pageable pageable);

}
