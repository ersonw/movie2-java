package com.telebott.movie2java.util;

import com.telebott.movie2java.dao.AuthDao;
import com.telebott.movie2java.entity.CashInOrder;
import com.telebott.movie2java.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.ZoneOffset;
import java.util.TimeZone;

@Configurable
@Component
@Slf4j
public class EPayUtil {
    public static final int MEMBERSHIP_ORDER = 1;
    public static final int CASH_ORDER = 2;
    public static final int DIAMOND_ORDER = 3;
    public static final int GAME_ORDER = 4;
    public static final int COIN_ORDER = 5;
    private static EPayUtil self;
    @Autowired
    private GameService gameService;
    @Autowired
    private DiamondService diamondService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private CashService cashService;
    @Autowired
    private MembershipService membershipService;
    @Autowired
    private AuthDao authDao;

    @PostConstruct
    public void init(){
        self = this;
    }
    public static boolean handlerOrder(CashInOrder cOrder) {
        self.authDao.pushInfo(1,cOrder.getOrderType());
        switch (cOrder.getOrderType()){
            case CASH_ORDER:
                return self.cashService.handlerOrder(cOrder.getOrderNo());
            case DIAMOND_ORDER:
                return self.diamondService.handlerOrder(cOrder.getOrderNo());
            case GAME_ORDER:
                return self.gameService.handlerOrder(cOrder.getOrderNo());
            case MEMBERSHIP_ORDER:
                return self.membershipService.handlerOrder(cOrder.getOrderNo());
            case COIN_ORDER:
                return self.coinService.handlerOrder(cOrder.getOrderNo());
            default:
                return false;
        }
    }
}
