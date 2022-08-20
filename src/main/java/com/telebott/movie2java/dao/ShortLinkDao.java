package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.ShortLink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface ShortLinkDao extends JpaRepository<ShortLink, Long>, CrudRepository<ShortLink, Long> {
    long countAllByStatus(int status);
    Page<ShortLink> findAllByStatus(int status, Pageable pageable);
}
