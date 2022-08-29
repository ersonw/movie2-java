package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.VideoConcentrationList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface VideoConcentrationListDao extends JpaRepository<VideoConcentrationList, Long>, CrudRepository<VideoConcentrationList, Long> {
    @Query(value = "SELECT vcl.* FROM `video_concentration_list` AS vcl INNER JOIN `video` v ON v.id=vcl.video_id INNER JOIN `video_concentration` vc ON vc.id=vcl.concentration_id GROUP BY concentration_id", nativeQuery = true)
    List<VideoConcentrationList> getAllByGroup();

}
