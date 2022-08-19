package com.telebott.movie2java.util;

import com.telebott.movie2java.service.ShortVideoService;
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
public class ShortLinkUtil {
    public static ShortLinkUtil self;
    @Autowired
    public ShortVideoService shortVideoService;

    @PostConstruct
    public void init(){
        self = this;
        rest();
        log.info("ShortLinkUtil initialized.");
    }
    public void rest(){}
}
