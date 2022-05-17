package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends JpaRepository<User, Long>, CrudRepository<User, Long> {
    User findAllById(long id);
}
