package com.telebott.movie2java.service;

import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AgentService {
    public static final String SPREAD = "spread";
    public static final String SPREAD_LEVEL_ONE = "spreadV1";
    public static final String SPREAD_LEVEL_TWO = "spreadV2";
    public static final String SPREAD_LEVEL_THREE = "spreadV3";
    public static final String SPREAD_HIDDEN = "hidden";
    public static final String SPREAD_LEVEL_MINI_HIDDEN = "hiddenMini";
    public static final String SPREAD_LEVEL_ONE_HIDDEN = "hiddenV1";
    public static final String SPREAD_LEVEL_TWO_HIDDEN = "hiddenV2";
    public static final String SPREAD_LEVEL_THREE_HIDDEN = "hiddenV3";
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

    public void handlerAgent(UserConsume consume){
        AgentRecord record1 = agentRecordDao.findAllByUserId(consume.getUserId());
        List<AgentRebate> rebates = new ArrayList<>();
        if (record1 != null && getConfigBool(SPREAD)){
            Agent agent1 = agentDao.findAllById(record1.getAgentId());
            if (agent1!= null){
                Double amount = 0D;
                if (agent1.getRebate() > 0){
                    amount = consume.getAmount() * agent1.getRebate();
                }else{
                    amount = getAgentAmount(consume, SPREAD_LEVEL_ONE);
                }
                if (amount > 0){
                    rebates.add(new AgentRebate(consume.getId(),agent1.getId(), amount,getAgentHidden(agent1.getId(),SPREAD_LEVEL_ONE,agent1.getHide())));
                }
            }
        }
        agentRebateDao.saveAllAndFlush(rebates);
    }
    public void handlerUser(UserConsume consume){
        UserSpreadRecord record1 = userSpreadRecordDao.findAllByUserId(consume.getUserId());
//        User user = userDao.findAllById(consume.getUserId());
        List<UserSpreadRebate> rebates = new ArrayList<>();
        List<UserBalanceCash> cashes = new ArrayList<>();
        if (record1 != null && getUserConfigBool(SPREAD)){
            User user1 = userDao.findAllById(record1.getShareUserId());
            if (user1 != null) {
                Double amount = getUserAmount(consume, SPREAD_LEVEL_ONE);
                if (amount > 0){
                    cashes.add(new UserBalanceCash(user1.getId(),amount,"来自一级推广返利"));
                    rebates.add(new UserSpreadRebate(consume.getId(),user1.getId(),amount,getUserHidden(user1.getId(), SPREAD_LEVEL_ONE_HIDDEN)));
                }
            }
            UserSpreadRecord record2 = userSpreadRecordDao.findAllByUserId(record1.getShareUserId());
            if (record2!= null){
                User user2= userDao.findAllById(record2.getShareUserId());
                if (user2 != null) {
                    Double amount = getUserAmount(consume, SPREAD_LEVEL_TWO);
                    if (amount > 0){
                        cashes.add(new UserBalanceCash(user2.getId(),amount,"来自二级级推广返利"));
                        rebates.add(new UserSpreadRebate(consume.getId(),user2.getId(),amount,getUserHidden(user2.getId(), SPREAD_LEVEL_TWO_HIDDEN)));
                    }
                }
                UserSpreadRecord record3 = userSpreadRecordDao.findAllByUserId(record2.getShareUserId());
                if (record3!= null){
                    User user3= userDao.findAllById(record3.getShareUserId());
                    if (user3 != null) {
                        Double amount = getUserAmount(consume, SPREAD_LEVEL_THREE);
                        if (amount > 0){
                            cashes.add(new UserBalanceCash(user3.getId(),amount,"来自三级级推广返利"));
                            rebates.add(new UserSpreadRebate(consume.getId(),user3.getId(),amount,getUserHidden(user3.getId(), SPREAD_LEVEL_THREE_HIDDEN)));
                        }
                    }
                }
            }
        }
//        userBalanceCashDao.saveAllAndFlush(cashes);
//        System.out.println(rebates);
        userSpreadRebateDao.saveAllAndFlush(rebates);
        handlerAgent(consume);
    }
    public int getAgentHidden(long userId, String level, double hidden){
        if (!getConfigBool(SPREAD_HIDDEN)) return 1;
        long spread = getConfigLong(level);
        if (spread <= 0 && hidden == 0) return 1;
        long all = agentRebateDao.countAllByAgentId(userId);
        long today = agentRebateDao.countAllByAgentIdAndAddTimeGreaterThanEqual(userId, TimeUtil.getTodayZero());
        long fail = agentRebateDao.countAllByAgentIdAndStatusAndAddTimeGreaterThanEqual(userId,0,TimeUtil.getTodayZero());
        if (all < getConfigLong(SPREAD_LEVEL_MINI_HIDDEN)) return 1;
        if(hidden > 0){
            if (hidden > (fail * 1D / today)) return 1;
        }else {
            if ((spread / 100D) > (fail * 1D / today)) return 1;
        }
        return 0;
    }
    public Double getAgentAmount(UserConsume consume, String level){
        long spread = getConfigLong(level);
        if(spread <= 0) return 0.0;
        double total = consume.getAmount() * 1D * (spread / 100D);
//        System.out.println(consume.getAmount());
//        System.out.println(String.format("%.2f", total));
        return new Double(String.format("%.2f", total));
    }
    public int getUserHidden(long userId, String level){
        if (!getUserConfigBool(SPREAD_HIDDEN)) return 1;
        long spread = getUserConfigLong(level);
        if (spread <= 0) return 1;
        long all = userSpreadRebateDao.countAllByUserId(userId);
        long today = userSpreadRebateDao.countAllByUserIdAndAddTimeGreaterThanEqual(userId, TimeUtil.getTodayZero());
        long fail = userSpreadRebateDao.countAllByUserIdAndStatusAndAddTimeGreaterThanEqual(userId,0,TimeUtil.getTodayZero());
        if (all < getUserConfigLong(SPREAD_LEVEL_MINI_HIDDEN)) return 1;
        if ((spread / 100D) > (fail * 1D / today)) return 1;
        return 0;
    }
    public Double getUserAmount(UserConsume consume, String level){
        long spread = getUserConfigLong(level);
        if(spread <= 0) return 0.0;
        double total = consume.getAmount() * 1D * (spread / 100D);
//        System.out.println(consume.getAmount());
//        System.out.println(String.format("%.2f", total));
        return new Double(String.format("%.2f", total));
    }
    public boolean getConfigBool(String name) {
        return getConfigLong(name) > 0;
    }

    public long getConfigLong(String name) {
        String value = getConfig(name);
        if (StringUtils.isEmpty(value)) return 0;
        return Long.parseLong(value);
    }
    public double getConfigDouble(String name) {
        String value = getConfig(name);
        if (StringUtils.isEmpty(value)) return 0D;
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
        if (StringUtils.isEmpty(value)) return 0;
        return Long.parseLong(value);
    }
    public double getUserConfigDouble(String name) {
        String value = getUserConfig(name);
        if (StringUtils.isEmpty(value)) return 0D;
        return Double.parseDouble(value);
    }

    public String getUserConfig(String name) {
        List<UserSpreadConfig> configs = userSpreadConfigDao.findAllByName(name);
        return configs.isEmpty() ? null : configs.get(0).getVal();
    }

}
