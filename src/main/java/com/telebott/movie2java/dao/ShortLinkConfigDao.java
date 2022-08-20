package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.ShortLinkConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ShortLinkConfigDao  extends JpaRepository<ShortLinkConfig, Long>, CrudRepository<ShortLinkConfig, Long> {
    List<ShortLinkConfig> findAllByName(String name);
    ShortLinkConfig findAllById(Long id);
}
