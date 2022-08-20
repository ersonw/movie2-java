package com.telebott.movie2java.service;

import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.entity.ShortLink;
import com.telebott.movie2java.entity.ShortLinkConfig;
import com.telebott.movie2java.entity.ShortLinkRecord;
import com.telebott.movie2java.entity.UserConfig;
import com.telebott.movie2java.util.ShortLinkUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ShortLinkService {
    @Autowired
    private ShortDao shortDao;
    @Autowired
    private ShortLinkDao shortLinkDao;
    @Autowired
    private ShortLinkConfigDao shortLinkConfigDao;
    @Autowired
    private ShortLinkRecordDao shortLinkRecordDao;
    @Autowired
    private ShortLinkDomainDao shortLinkDomainDao;
    public boolean getConfigBool(String name){
        return getConfigLong(name) > 0;
    }
    public long getConfigLong(String name){
        String value = getConfig(name);
        if(value == null) return 0;
        return Long.parseLong(value);
    }
    public String getConfig(String name){
        List<ShortLinkConfig> configs = shortLinkConfigDao.findAllByName(name);
        return configs.isEmpty() ? null : configs.get(0).getVal();
    }
    public List<String> getBlacklist(String name){
        List<ShortLinkConfig> configs = shortLinkConfigDao.findAllByName(name);
        List<String> blacklist = new ArrayList<>();
        for(ShortLinkConfig config : configs){
            blacklist.add(config.getVal());
        }
        return blacklist;
    }
    public List<ShortLink> getLinks(){
        List<ShortLink> links = new ArrayList<>();
        int page = 0;
        Pageable pageable = PageRequest.of(page,200, Sort.by(Sort.Direction.DESC,"addTime"));
        Page<ShortLink> linkPage = shortLinkDao.findAllByStatus(1,pageable);
        if (linkPage.getContent().size() > 0) links.addAll(linkPage.getContent());
        while (page+1 < linkPage.getTotalPages()){
            page++;
            linkPage = shortLinkDao.findAllByStatus(1,pageable);
            if (linkPage.getContent().size() > 0) links.addAll(linkPage.getContent());
        }
        return links;
    }
//    public String generate(long userId, String url){}
    public void search(String id, String url, String userAgent, String ip, HttpServletResponse response) {
//        System.out.println(ip);
        try {
            if(StringUtils.isEmpty(url)) {
                response.sendError(404);
                return;
            }
            if(StringUtils.isEmpty(userAgent)) {
                response.sendError(404);
                return;
            }
            if(StringUtils.isEmpty(ip)) {
                response.sendError(404);
                return;
            }
            if (ShortLinkUtil.blackUserAgents.contains(userAgent)) {
                response.sendError(404);
                return;
            }
            if (ShortLinkUtil.blackIPs.contains(ip)) {
                response.sendError(404);
                return;
            }
            ShortLink link = shortDao.findById(id);
            if(link == null || link.getStatus() != 1) {
                response.sendError(404);
                return;
            }
            if (!url.equals(link.getLink())) {
                response.sendError(404);
                return;
            }
            response.sendRedirect(link.getLink());
            shortLinkRecordDao.saveAndFlush(new ShortLinkRecord(link.getId(),userAgent,ip));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
