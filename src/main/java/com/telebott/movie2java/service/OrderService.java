package com.telebott.movie2java.service;

import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.ToPayNotify;
import com.telebott.movie2java.entity.CashInOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    public static final int ORDER_TYPE_MEMBERSHIP = 0;
    public static final int ORDER_TYPE_CASH = 1;
    public static final int ORDER_TYPE_DIAMOND = 2;
    public static final int ORDER_TYPE_GAME = 3;

    @Autowired
    private CashInOrderDao cashInOrderDao;
    @Autowired
    private MembershipOrderDao membershipOrderDao;
    @Autowired
    private CashOrderDao cashOrderDao;
    @Autowired
    private DiamondOrderDao diamondOrderDao;
    @Autowired
    private GameOrderDao gameOrderDao;

    public boolean handlerToPayNotify(CashInOrder order) {
        order.setStatus(1);
        order.setUpdateTime(System.currentTimeMillis());
//        cashInOrderDao.saveAndFlush(order);
        return true;
    }
}
