package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface UserDao extends JpaRepository<User, Long>, CrudRepository<User, Long> {
    User findAllById(long id);

    User findByUsername(String username);
    User findAllByNickname(String nickname);
    User findAllByEmail(String email);

    User findAllByPhone(String phone);
    User findByPhone(String phone);
    @Query(value = "SELECT u.* FROM `user` AS u INNER JOIN user_robot ur ON u.id = ur.user_id", nativeQuery = true)
    List<User> getUserList();
    @Query(value = "SELECT u.* FROM `membership_expired` AS me INNER JOIN `user` u ON u.id=me.user_id WHERE me.user_id=:userId AND me.expired >:time ", nativeQuery = true)
    User getAllByMembership(long userId, long time);
}
