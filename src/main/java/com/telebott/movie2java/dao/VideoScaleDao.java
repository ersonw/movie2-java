package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.VideoScale;
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
public interface VideoScaleDao extends JpaRepository<VideoScale, Long>, CrudRepository<VideoScale, Long> {
//    @Query(value = "select * from users where expireds > :time ", nativeQuery = true)
//    Page<VideoScale> findAll(long time , Pageable pageable);
//     @Modifying
//     @Query(value = "UPDATE `videos` SET actor=0 WHERE actor=:aid", nativeQuery = true)
//     void removeAllByAid(long aid);
}
