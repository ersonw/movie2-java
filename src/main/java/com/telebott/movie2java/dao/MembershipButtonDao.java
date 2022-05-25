package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.MembershipButton;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface MembershipButtonDao extends JpaRepository<MembershipButton, Long>, CrudRepository<MembershipButton, Long> {
}
