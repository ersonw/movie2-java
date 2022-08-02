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
    Long countAllByUserId(long userId);
    Long countAllByUserIdAndStatus(long userId, int status);
    List<ShortVideo> findAllByFile(String file);
    List<ShortVideo> findAllByFileAndUserId(String file, long userId);
    @Query(value = "SELECT * FROM \n" +
            "(SELECT sv.* FROM `short_video` AS sv\n" +
            "WHERE  sv.user_id =:userId\n" +
            "ORDER BY sv.id DESC) as s \n" +
            "ORDER BY s.pin DESC",nativeQuery = true)
    Page<ShortVideo> getAllMyVideos(long userId, Pageable pageable);
    @Query(value = "SELECT * FROM \n" +
            "(SELECT sv.* FROM `short_video` AS sv\n" +
            "WHERE  sv.user_id =:userId AND sv.status =1\n" +
            "ORDER BY sv.id DESC) as s \n" +
            "ORDER BY s.pin DESC",nativeQuery = true)
    Page<ShortVideo> getAllProfileVideos(long userId, Pageable pageable);
    @Query(value = "SELECT s.* FROM (SELECT sv.* FROM `short_video_like` AS svl INNER JOIN `short_video` AS sv ON sv.status=1 WHERE  svl.user_id =:userId ) AS s WHERE s.user_id <> :userId GROUP BY s.id ORDER BY s.add_time DESC",nativeQuery = true)
    Page<ShortVideo> getAllLikeProfileVideos(long userId, Pageable pageable);
    @Query(value = "SELECT *,(SELECT COUNT(*) FROM `short_video_play` WHERE sv.id=video_id) as c FROM `short_video` as `sv` WHERE (select count(*) from `short_video_play` where video_id = sv.id AND user_id = :userId) = 0 and sv.status = 1 ORDER BY c DESC",nativeQuery = true)
    Page<ShortVideo> getAllVideos(long userId, Pageable pageable);
    @Query(value = "SELECT *,(SELECT COUNT(*) FROM `short_video_play` WHERE sv.id=video_id) as c FROM `short_video` as `sv` WHERE sv.status = 1 ORDER BY c DESC",nativeQuery = true)
    Page<ShortVideo> getAllVideos(Pageable pageable);


    @Query(value = "SELECT sv.* FROM `short_video` as `sv` INNER JOIN (SELECT * FROM `user_follow` WHERE user_id = :userId) uf ON uf.to_user_id = sv.user_id WHERE (select count(*) from `short_video_play` where video_id = sv.id) = 0 and sv.status = 1",nativeQuery = true)
    Page<ShortVideo> getAllByForwards(long userId, Pageable pageable);
    @Query(value = "SELECT sv.* FROM `short_video` as `sv` INNER JOIN (SELECT * FROM `user_follow` WHERE user_id = :userId) uf ON uf.to_user_id = sv.user_id WHERE  sv.status = 1",nativeQuery = true)
    Page<ShortVideo> getAllByForward(long userId, Pageable pageable);
    @Query(value = "SELECT sv.* FROM `short_video` as `sv` WHERE (select count(*) from `short_video_play` where video_id = sv.id) = 0 and sv.user_id =:userId and sv.status = 1",nativeQuery = true)
    Page<ShortVideo> getAllByUser(long userId, Pageable pageable);
}
