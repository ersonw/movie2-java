package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.MembershipBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface MembershipBenefitDao extends JpaRepository<MembershipBenefit, Long>, CrudRepository<MembershipBenefit, Long> {
    MembershipBenefit findAllById(Long id);
}
