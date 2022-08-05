package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserFollowDao extends JpaRepository<UserFollow, Long>, CrudRepository<UserFollow, Long> {
    UserFollow findAllByUserIdAndToUserId(Long userId, Long toUserId);
    Long countAllByUserId(Long userId);
    Page<UserFollow> findAllByUserId(long userId, Pageable pageable);
    Page<UserFollow> findAllByToUserId(long userId, Pageable pageable);
    Long countAllByToUserId(Long userId);
    Long countAllByToUserIdAndState(Long userId, int state);
    @Query(value = "SELECT * FROM (SELECT uf.* FROM `user` AS u INNER JOIN user_follow AS uf ON uf.user_id=:userId AND uf.to_user_id=u.id WHERE  u.nickname LIKE :text) AS s",nativeQuery = true)
    Page<UserFollow> getAllByUserId(Long userId, String text, Pageable pageable);
    @Query(value = "SELECT * FROM (SELECT uf.* FROM `user` AS u INNER JOIN user_follow AS uf ON uf.to_user_id=:userId AND uf.user_id=u.id WHERE  u.nickname LIKE :text) AS s",nativeQuery = true)
    Page<UserFollow> getAllByToUserId(Long userId, String text, Pageable pageable);
}
