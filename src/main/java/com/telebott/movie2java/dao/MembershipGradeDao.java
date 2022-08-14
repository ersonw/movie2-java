package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.MembershipGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MembershipGradeDao extends JpaRepository<MembershipGrade, Long>, CrudRepository<MembershipGrade, Long> {
    @Query(value = "SELECT * FROM `membership_grade` WHERE status=1 ORDER BY `mini` ASC ",nativeQuery = true)
    List<MembershipGrade> getAllGrades();
}
