package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.SmsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface SmsRecordDao extends JpaRepository<SmsRecord, Long>, CrudRepository<SmsRecord, Long> {
    SmsRecord findAllByData(String data);
    @Query(value = "SELECT COUNT(*) FROM `sms_record` where phone =:phone and add_time > :cTime", nativeQuery = true)
    Long countTodayMax(@Param(value = "cTime")long cTime,@Param(value = "phone") String phone);
    SmsRecord findAllById(int id);
    @Query(value = "SELECT * FROM `sms_record` where phone =:phone and add_time > :time and status = 0 order by id desc  LIMIT 1", nativeQuery = true)
    SmsRecord findByNumberCodeFromTime(@Param(value = "phone")String phone, @Param(value = "time")long time);
    @Query(value = "SELECT * FROM `sms_record` where phone =:phone and code = :code and add_time > :time and status =0 order by id asc  LIMIT 1", nativeQuery = true)
    SmsRecord findByNumberCodeFirst(@Param(value = "phone")String phone, @Param(value = "code")String code, @Param(value = "time")long time);
    @Query(value = "SELECT * FROM `sms_record` where phone =:phone and code = :code order by id desc LIMIT 1", nativeQuery = true)
    SmsRecord findByNumberCode(@Param(value = "phone")String phone, @Param(value = "code")String code);
    @Query(value = "SELECT * FROM `sms_record` where phone =:phone order by id desc LIMIT 1", nativeQuery = true)
    SmsRecord getLast(@Param(value = "phone")String phone);
}
