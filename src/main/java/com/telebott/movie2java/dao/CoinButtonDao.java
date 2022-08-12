package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.CoinButton;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
@Repository
public interface CoinButtonDao extends JpaRepository<CoinButton, Long>, CrudRepository<CoinButton, Long> {
    CoinButton findAllById(Long id);
    @Query(value = "SELECT * FROM `coin_button` WHERE status=1 ORDER BY amount ASC ",nativeQuery = true)
    List<CoinButton> getAllButtons();
}
