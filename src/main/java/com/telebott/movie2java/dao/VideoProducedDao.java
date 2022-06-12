package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.VideoProduced;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface VideoProducedDao extends JpaRepository<VideoProduced, Long>, CrudRepository<VideoProduced, Long> {
    List<VideoProduced> findAllByStatus(int status);
}
