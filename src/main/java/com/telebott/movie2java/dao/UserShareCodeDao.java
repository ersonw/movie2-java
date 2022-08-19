package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.UserShareCode;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserShareCodeDao extends JpaRepository<UserShareCode, Long>, CrudRepository<UserShareCode, Long> {
    Page<UserShareCode> findAllByUserId(long userId);
}
