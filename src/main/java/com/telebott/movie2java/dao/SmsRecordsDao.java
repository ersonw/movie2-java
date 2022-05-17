package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.SmsRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsRecordsDao extends JpaRepository<SmsRecords, Long>, CrudRepository<SmsRecords, Long> {
    SmsRecords findAllByData(String data);
    @Query(value = "SELECT COUNT(*) FROM `sms_records` where number =:phone and ctime > :cTime", nativeQuery = true)
    Long countTodayMax(@Param(value = "cTime")long cTime,@Param(value = "phone") String phone);
    SmsRecords findAllById(int id);
    @Query(value = "SELECT * FROM `sms_records` where number =:phone and time > :time and status = 0 order by id desc  LIMIT 1", nativeQuery = true)
    SmsRecords findByNumberCodeFromTime(@Param(value = "phone")String phone, @Param(value = "time")long time);
    @Query(value = "SELECT * FROM `sms_records` where number =:phone and code = :code and time > :time and status =0 order by id asc  LIMIT 1", nativeQuery = true)
    SmsRecords findByNumberCodeFirst(@Param(value = "phone")String phone, @Param(value = "code")String code, @Param(value = "time")long time);
    @Query(value = "SELECT * FROM `sms_records` where number =:phone and code = :code order by id desc LIMIT 1", nativeQuery = true)
    SmsRecords findByNumberCode(@Param(value = "phone")String phone, @Param(value = "code")String code);
}
