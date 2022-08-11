package com.telebott.movie2java.dao;

import com.telebott.movie2java.entity.CashInOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface CashInOrderDao extends JpaRepository<CashInOrder, Long>, CrudRepository<CashInOrder, Long> {
    CashInOrder findAllByOrderNo(String orderNo);
    CashInOrder findAllByOrderNoAndStatus(String orderNo, int status);
    @Query(value = "SELECT cio.* FROM `cash_in_order` AS cio INNER JOIN `game_order` go ON go.order_no = cio.order_no AND go.user_id =:userId WHERE cio.order_type=4",nativeQuery = true)
    Page<CashInOrder> getAllByGame(long userId, Pageable pageable);
    @Query(value = "SELECT cio.* FROM `cash_in_order` AS cio INNER JOIN `diamond_order` go ON go.order_no = cio.order_no AND go.user_id =:userId WHERE cio.order_type=4",nativeQuery = true)
    Page<CashInOrder> getAllByDiamond(long userId, Pageable pageable);
    @Query(value = "DELETE game_order FROM game_order LEFT JOIN cash_in_order ON game_order.order_no=cash_in_order.order_no WHERE cash_in_order.id IS NULL",nativeQuery = true)
    @Modifying
    void deleteAllByGameOrder();
    @Query(value = "DELETE membership_order FROM membership_order LEFT JOIN cash_in_order ON membership_order.order_no=cash_in_order.order_no WHERE cash_in_order.id IS NULL",nativeQuery = true)
    @Modifying
    void deleteAllByMembershipOrder();
    @Query(value = "DELETE cash_order FROM cash_order LEFT JOIN cash_in_order ON cash_order.order_no=cash_in_order.order_no WHERE cash_in_order.id IS NULL",nativeQuery = true)
    @Modifying
    void deleteAllByCashOrder();
    @Query(value = "DELETE diamond_order FROM diamond_order LEFT JOIN cash_in_order ON diamond_order.order_no=cash_in_order.order_no WHERE cash_in_order.id IS NULL",nativeQuery = true)
    @Modifying
    void deleteAllByDiamondOrder();
}
