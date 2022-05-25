package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.FilterWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface FilterWordsDao extends JpaRepository<FilterWords, Long>, CrudRepository<FilterWords, Long> {
}
