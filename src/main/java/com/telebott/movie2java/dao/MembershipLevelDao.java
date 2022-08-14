package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.MembershipLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface MembershipLevelDao extends JpaRepository<MembershipLevel, Long>, CrudRepository<MembershipLevel, Long> {
    MembershipLevel findAllById(Long id);
    MembershipLevel findByLevel(long level);
}
