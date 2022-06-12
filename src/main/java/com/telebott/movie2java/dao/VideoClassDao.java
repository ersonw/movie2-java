package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.VideoClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface VideoClassDao extends JpaRepository<VideoClass, Long>, CrudRepository<VideoClass, Long> {
    VideoClass findAllByName(String name);
    @Modifying
    @Query(value = "SELECT vc.* FROM (SELECT * FROM `video` WHERE status=:status AND vod_class > 0 GROUP BY `vod_class`) v LEFT JOIN `video_class` vc ON v.vod_class=vc.id ", nativeQuery = true)
    List<VideoClass> findAllByStatus(int status);
//    @Modifying
//    @Query(value = "DELETE vc.*,vcl.* FROM video_comment as vc LEFT JOIN video_comment_like as vcl ON vcl.comment_id=vc.id WHERE vc.reply_id =:id OR vc.id=:id", nativeQuery = true)
//    void removeAllById(long id);
}
