package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.UserSpreadRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserSpreadRecordDao extends JpaRepository<UserSpreadRecord, Long>, CrudRepository<UserSpreadRecord, Long> {
    UserSpreadRecord findAllByUserId(long userId);
    UserSpreadRecord findAllByUserIdAndShareUserId(long userId, long toUserId);

    Long countByUserId(long id);
    @Query(value = "SELECT COUNT(*) FROM `user_spread_record` AS usr INNER JOIN user AS u ON u.id=usr.share_user_id AND  isnull(u.phone) = 0 WHERE usr.user_id=:id ",nativeQuery = true)
    Long findAllByCount(long id);

    Long countByShareUserId(long id);
}
