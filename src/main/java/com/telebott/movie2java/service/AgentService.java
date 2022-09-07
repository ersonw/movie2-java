package com.telebott.movie2java.service;

import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AgentService {
    public static final String USER_SPREAD_LEVEL_ONE = "spreadV1";
    public static final String USER_SPREAD_LEVEL_TWO = "spreadV2";
    public static final String USER_SPREAD_LEVEL_THREE = "spreadV3";
    public static final String USER_SPREAD_LEVEL_ONE_HIDDEN = "hiddenV1";
    public static final String USER_SPREAD_LEVEL_TWO_HIDDEN = "hiddenV2";
    public static final String USER_SPREAD_LEVEL_THREE_HIDDEN = "hiddenV3";
    @Autowired
    private AgentDao agentDao;
    @Autowired
    private AgentBalanceCashDao AgentBalanceCashDao;
    @Autowired
    private AgentChannelDao agentChannelDao;
    @Autowired
    private AgentConfigDao agentConfigDao;
    @Autowired
    private AgentRebateDao agentRebateDao;
    @Autowired
    private AgentRecordDao agentRecordDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserConsumeDao userConsumeDao;
    @Autowired
    private UserBalanceCashDao userBalanceCashDao;
    @Autowired
    private UserSpreadConfigDao userSpreadConfigDao;
    @Autowired
    private UserSpreadRebateDao userSpreadRebateDao;
    @Autowired
    private UserSpreadRecordDao userSpreadRecordDao;

    public void handlerUser(UserConsume consume){
        UserSpreadRecord record1 = userSpreadRecordDao.findAllByUserId(consume.getUserId());
//        User user = userDao.findAllById(consume.getUserId());
        List<UserSpreadRebate> rebates = new ArrayList<>();
        List<UserBalanceCash> cashes = new ArrayList<>();
        if (record1 != null){
            User user1 = userDao.findAllById(record1.getShareUserId());
            if (user1 != null) {
                Double amount = getUserAmount(consume, USER_SPREAD_LEVEL_ONE);
                cashes.add(new UserBalanceCash(user1.getId(),amount,"来自一级推广返利"));
                rebates.add(new UserSpreadRebate(consume.getId(),user1.getId(),amount,getUserHidden(user1.getId(), USER_SPREAD_LEVEL_ONE_HIDDEN)));
            }
            UserSpreadRecord record2 = userSpreadRecordDao.findAllByUserId(record1.getShareUserId());
            if (record2!= null){
                User user2= userDao.findAllById(record2.getShareUserId());
                if (user2 != null) {
                    Double amount = getUserAmount(consume, USER_SPREAD_LEVEL_TWO);
                    cashes.add(new UserBalanceCash(user2.getId(),amount,"来自二级级推广返利"));
                    rebates.add(new UserSpreadRebate(consume.getId(),user2.getId(),amount,getUserHidden(user2.getId(), USER_SPREAD_LEVEL_TWO_HIDDEN)));
                }
                UserSpreadRecord record3 = userSpreadRecordDao.findAllByUserId(record2.getShareUserId());
                if (record3!= null){
                    User user3= userDao.findAllById(record3.getShareUserId());
                    if (user3 != null) {
                        Double amount = getUserAmount(consume, USER_SPREAD_LEVEL_THREE);
                        cashes.add(new UserBalanceCash(user3.getId(),amount,"来自三级级推广返利"));
                        rebates.add(new UserSpreadRebate(consume.getId(),user3.getId(),amount,getUserHidden(user3.getId(), USER_SPREAD_LEVEL_THREE_HIDDEN)));
                    }
                }
            }
        }
//        userBalanceCashDao.saveAllAndFlush(cashes);
        userSpreadRebateDao.saveAllAndFlush(rebates);
    }
    public int getUserHidden(long userId, String level){
        long spread = getUserConfigLong(level);
        long all = userSpreadRebateDao.countAllByUserId(userId);
        long today = userSpreadRebateDao.countAllByUserIdAndAddTimeGreaterThanEqual(userId, TimeUtil.getTodayZero());
        long fail = userSpreadRebateDao.countAllByUserIdAndStatusAndAddTimeGreaterThanEqual(userId,0,TimeUtil.getTodayZero());
        if (all < 9) return 1;
        if (spread < new Double((fail * 1D) / today * 100).longValue()) return 1;
        return 0;
    }
    public Double getUserAmount(UserConsume consume, String level){
        long spread = getUserConfigLong(level);
        return new Double(consume.getAmount() / spread);
    }
    public boolean getConfigBool(String name) {
        return getConfigLong(name) > 0;
    }

    public long getConfigLong(String name) {
        String value = getConfig(name);
        if (value == null) return 0;
        return Long.parseLong(value);
    }
    public double getConfigDouble(String name) {
        String value = getConfig(name);
        if (value == null) return 0D;
        return Double.parseDouble(value);
    }

    public String getConfig(String name) {
        List<AgentConfig> configs = agentConfigDao.findAllByName(name);
        return configs.isEmpty() ? null : configs.get(0).getVal();
    }
    public boolean getUserConfigBool(String name) {
        return getUserConfigLong(name) > 0;
    }

    public long getUserConfigLong(String name) {
        String value = getUserConfig(name);
        if (value == null) return 0;
        return Long.parseLong(value);
    }
    public double getUserConfigDouble(String name) {
        String value = getUserConfig(name);
        if (value == null) return 0D;
        return Double.parseDouble(value);
    }

    public String getUserConfig(String name) {
        List<UserSpreadConfig> configs = userSpreadConfigDao.findAllByName(name);
        return configs.isEmpty() ? null : configs.get(0).getVal();
    }

}
