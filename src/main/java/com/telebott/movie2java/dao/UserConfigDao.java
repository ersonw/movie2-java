package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserConfigDao extends JpaRepository<UserConfig, Long>, CrudRepository<UserConfig, Long> {

    UserConfig findAllById(long id);

    List<UserConfig> findAllByName(String name);
}
