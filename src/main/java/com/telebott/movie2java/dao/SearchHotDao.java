package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.SearchHot;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface SearchHotDao extends JpaRepository<SearchHot, Long>, CrudRepository<SearchHot, Long> {
    @Query(value = "SELECT `id`,`words`,`ip`,`user_id`,`add_time`,COUNT(*) AS c FROM `search_hot` GROUP BY words ORDER BY c DESC LIMIT :page,12", nativeQuery = true)
    List<SearchHot> getByHot(int page);

    long countByHot();
}
