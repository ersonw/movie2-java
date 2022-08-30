package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.Publicize;
import com.telebott.movie2java.entity.VideoPublicity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface VideoPublicityDao extends JpaRepository<VideoPublicity, Long>, CrudRepository<VideoPublicity, Long> {
    List<VideoPublicity> findAllByStatus(int status);
    VideoPublicity findAllById(long id);

}
