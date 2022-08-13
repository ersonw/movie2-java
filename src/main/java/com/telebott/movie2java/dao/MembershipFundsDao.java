package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.MembershipFunds;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface MembershipFundsDao  extends JpaRepository<MembershipFunds, Long>, CrudRepository<MembershipFunds, Long> {
    Page<MembershipFunds> findAllByUserId(long id, Pageable pageable);
}
