package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.VideoPpvod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface VideoPpvodDao extends JpaRepository<VideoPpvod, Long>, CrudRepository<VideoPpvod, Long> {
}
