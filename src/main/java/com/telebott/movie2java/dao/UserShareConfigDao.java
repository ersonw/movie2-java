package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.UserShareConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface UserShareConfigDao extends JpaRepository<UserShareConfig, Long>, CrudRepository<UserShareConfig, Long> {
    UserShareConfig findAllById(long id);
    List<UserShareConfig> findAllByName(String name);
}
