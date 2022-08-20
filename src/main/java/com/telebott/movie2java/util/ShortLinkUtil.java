package com.telebott.movie2java.util;

import com.telebott.movie2java.dao.ShortDao;
import com.telebott.movie2java.entity.ShortLink;
import com.telebott.movie2java.service.ShortLinkService;
import com.telebott.movie2java.service.ShortVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Configurable
@Component
@Slf4j
public class ShortLinkUtil {
    public static ShortLinkUtil self;
    public static List<String> blackUserAgents = new ArrayList<>();
    public static List<String> blackIPs = new ArrayList<>();
    @Autowired
    private ShortLinkService service;
    @Autowired
    private ShortDao shortDao;

    @PostConstruct
    public void init(){
        self = this;
        rest();
    }
    public void rest(){
        blackUserAgents = self.service.getBlacklist("userAgent");
        blackIPs = self.service.getBlacklist("ip");
        List<ShortLink> links = self.service.getLinks();
        for(ShortLink link : links){
            if (!shortDao.contains(link)){
                shortDao.pushData(link);
            }
        }
        log.info("短链接初始化成功.");
    }
}
