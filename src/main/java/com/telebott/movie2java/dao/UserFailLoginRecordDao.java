package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.UserFailLoginRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
@Repository
public interface UserFailLoginRecordDao extends JpaRepository<UserFailLoginRecord, Long>, CrudRepository<UserFailLoginRecord, Long> {
    List<UserFailLoginRecord> findAllByUserId(long userId);
    @Query(value = "SELECT * FROM `user_fail_login_record` WHERE `user_id`=:userId and `add_time` > :time", nativeQuery = true)
    List<UserFailLoginRecord> checkUserToday(long userId, long time);
    @Modifying
    @Query(value = "DELETE FROM `user_fail_login_record` WHERE `user_id`=:userId", nativeQuery = true)
    void deleteAllByUserId(long userId);
}
