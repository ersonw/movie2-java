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
    @Query(value = "SELECT s.*,COUNT(*) AS c FROM `search_hot` s GROUP BY words ORDER BY c DESC LIMIT :page,:limit", nativeQuery = true)
    List<SearchHot> getByHot(int page, int limit);
    @Query(value = "SELECT s.*,COUNT(*) AS c FROM `search_hot` s WHERE add_time >= :time GROUP BY words ORDER BY c DESC LIMIT :page,:limit", nativeQuery = true)
    List<SearchHot> getByHot(long time,int page, int limit);
    @Query(value = "select count(*) from (SELECT `words`,count(*) as count FROM search_hot group by `words`) T", nativeQuery = true)
    long countByHot();
}
