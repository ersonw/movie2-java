package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.ShortVideo;
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
public interface ShortVideoDao extends JpaRepository<ShortVideo, Long>, CrudRepository<ShortVideo, Long> {
    ShortVideo findAllById(Long videoId);
    List<ShortVideo> findAllByFile(String file);
    List<ShortVideo> findAllByFileAndUserId(String file, long userId);
    @Query(value = "SELECT *,(SELECT COUNT(*) FROM short_video_play WHERE sv.id=video_id) as c FROM short_video as sv WHERE (select count(*) from short_video_play where video_id = sv.id AND user_id = :userId) = 0 and sv.status = 1 ORDER BY c DESC",nativeQuery = true)
    Page<ShortVideo> getAllVideos(long userId, Pageable pageable);
    @Query(value = "SELECT *,(SELECT COUNT(*) FROM short_video_play WHERE sv.id=video_id) as c FROM short_video as sv WHERE (select count(*) from short_video_play where video_id = sv.id) = 0 and sv.status = 1 ORDER BY c DESC",nativeQuery = true)
    Page<ShortVideo> getAllVideos(Pageable pageable);


    @Query(value = "SELECT sv.* FROM short_video as sv INNER JOIN (SELECT * FROM `user_follow` WHERE user_id = :userId) uf ON uf.user_id = sv.user_id WHERE (select count(*) from short_video_play where video_id = sv.id) = 0 and sv.status = 1",nativeQuery = true)
    Page<ShortVideo> getAllByForwards(long userId, Pageable pageable);
    @Query(value = "SELECT sv.* FROM short_video as sv INNER JOIN (SELECT * FROM `user_follow` WHERE user_id = :userId) uf ON uf.user_id = sv.user_id WHERE  sv.status = 1",nativeQuery = true)
    Page<ShortVideo> getAllByForward(long userId, Pageable pageable);
    @Query(value = "SELECT sv.* FROM short_video as sv WHERE (select count(*) from short_video_play where video_id = sv.id) = 0 and sv.user_id =:userId and sv.status = 1",nativeQuery = true)
    Page<ShortVideo> getAllByUser(long userId, Pageable pageable);
}
