package com.telebott.movie2java.service;

import com.telebott.movie2java.dao.UserSpreadConfigDao;
import com.telebott.movie2java.dao.UserSpreadRebateDao;
import com.telebott.movie2java.dao.UserSpreadRecordDao;
import com.telebott.movie2java.entity.UserConfig;
import com.telebott.movie2java.entity.UserSpreadConfig;
import com.telebott.movie2java.entity.UserSpreadRebate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserSpreadService {
    @Autowired
    private UserSpreadConfigDao userSpreadConfigDao;
    @Autowired
    private UserSpreadRebateDao userSpreadRebateDao;
    @Autowired
    private UserSpreadRecordDao userSpreadRecordDao;
    public boolean getConfigBool(String name){
        return getConfigLong(name) > 0;
    }
    public long getConfigLong(String name){
        String value = getConfig(name);
        if(value == null) return 0;
        return Long.parseLong(value);
    }
    public String getConfig(String name){
        List<UserSpreadConfig> configs = userSpreadConfigDao.findAllByName(name);
        return configs.isEmpty() ? null : configs.get(0).getVal();
    }
}
