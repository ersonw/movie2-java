package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.ShortVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ShortVideoDao extends JpaRepository<ShortVideo, Long>, CrudRepository<ShortVideo, Long> {
    List<ShortVideo> findAllByFile(String file);
    List<ShortVideo> findAllByFileAndUserId(String file, long userId);
}
