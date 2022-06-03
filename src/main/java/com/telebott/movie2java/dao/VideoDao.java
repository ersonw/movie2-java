package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
