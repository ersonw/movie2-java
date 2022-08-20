package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.ShortLinkDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
@Repository
public interface ShortLinkDomainDao extends JpaRepository<ShortLinkDomain, Long>, CrudRepository<ShortLinkDomain, Long> {
    ShortLinkDomain findAllById(Long id);
    List<ShortLinkDomain> findAllByStatus(int status);
}
