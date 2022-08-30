package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.Publicize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface PublicizeDao extends JpaRepository<Publicize, Long>, CrudRepository<Publicize, Long> {
    List<Publicize> findAllByStatus(int status);
    Page<Publicize> findAllByStatus(int status, Pageable pageable);
    List<Publicize> findAllByPage(int page);
    List<Publicize> findAllByPageAndStatus(int page, int status);
    Publicize findAllById(long id);
}
