package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.DiamondButton;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DiamondButtonDao extends JpaRepository<DiamondButton, Long>, CrudRepository<DiamondButton, Long> {
    DiamondButton findAllById(Long id);
    @Query(value = "SELECT * FROM `diamond_button` WHERE status=1 ORDER BY amount ASC ",nativeQuery = true)
    List<DiamondButton> getAllButtons();
}
