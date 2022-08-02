package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.ShortVideoLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface ShortVideoLikeDao extends JpaRepository<ShortVideoLike, Long>, CrudRepository<ShortVideoLike, Long> {
    Long countAllByVideoId(long videoId);
    Long countAllByUserId(long userId);
    @Query(value ="SELECT IFNULL(SUM((SELECT COUNT(*) FROM `short_video_like` WHERE video_id=sv.id)),0) FROM short_video AS sv WHERE sv.user_id=:userId AND sv.status=1",nativeQuery = true)
    Long getAllByUserId(long userId);
    ShortVideoLike findAllByUserIdAndVideoId(long userId, long videoId);
}
