package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.SearchData;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SearchService {
    private static int COUNT_ANY_TIME = 0;
    private static final int LIMIT_ANY_TIME = 12;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private SearchHotDao hotDao;
    @Autowired
    private VideoDao videoDao;
    @Autowired
    private VideoPayDao videoPayDao;
    @Autowired
    private VideoPlayDao videoPlayDao;
    @Autowired
    private VideoLikeDao videoLikeDao;

    public ResponseData searchMovie(String text,User user, String ip) {
//        System.out.println(text);
        if (text == null || "".equals(text)) {
            return ResponseData.error("Invalid search text");
        }
        SearchData data = new SearchData(text);
        if (user != null){
            data.setUserId(user.getId());
//            SearchHot hot = new SearchHot();
//            hot.setAddTime(System.currentTimeMillis());
//            hot.setUserId(user.getId());
//            hot.setIp(ip);
//            hot.setWords(text);
//            hotDao.saveAndFlush(hot);
        }
        authDao.pushSearch(data);
        return ResponseData.success(ResponseData.object("id", data.getId()));
    }
    public ResponseData searchResult(String id, int page, User user, String ip) {
        if (id == null || "".equals(id)) {
            return ResponseData.error("Invalid search id");
        }
        SearchData data = authDao.findSearch(id);
        if (data == null) {
            return ResponseData.error("search ID is expired");
        }
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Video> videoPage = videoDao.findAllByTitleLikeAndStatus("%"+data.getText()+"%",1, pageable);
        JSONArray array = new JSONArray();
        for (Video video : videoPage.getContent()) {
            array.add(getVideo(video));
        }
        JSONObject object = ResponseData.object("list",array);
        object.put("total", videoPage.getTotalPages());
        object.put("text", data.getText());
        return ResponseData.success(object);
    }
    public JSONObject getVideo(Video video) {
        JSONObject json = new JSONObject();
        json.put("id", video.getId());
        json.put("title", video.getTitle());
        json.put("vodContent", video.getVodContent());
        json.put("picThumb", video.getPicThumb());
        json.put("vodDuration",video.getVodDuration());
        json.put("plays", video.getPlays()+ videoPlayDao.countAllByVideoId(video.getId()));
        json.put("likes", video.getLikes()+ videoLikeDao.countAllByVideoId(video.getId()));
        json.put("price", 0);
        VideoPay pay = videoPayDao.findAllByVideoId(video.getId());
        if (pay != null) {
            json.put("price", pay.getAmount());
        }
        return json;
    }

    public ResponseData searchCancel(String id,String ip) {
        SearchData data = authDao.findSearch(id);
        if (data != null && data.getUserId() > 0) {
            SearchHot hot = new SearchHot(0,data.getText(),ip, data.getUserId(),System.currentTimeMillis());
            hotDao.saveAndFlush(hot);
        }
        authDao.popSearch(data);
        return ResponseData.success();
    }

    public ResponseData labelAnytime(User user, String ip) {
//        log.info("[{}] labelAnytime userId:{} IP:{}", TimeUtil.getNowDate(), user.getId(), ip);
        Long count = hotDao.countByHot();
        if (((long) COUNT_ANY_TIME * LIMIT_ANY_TIME) > count) {
            COUNT_ANY_TIME = 0;
        }
        List<SearchHot> hots = hotDao.getByHot(COUNT_ANY_TIME*LIMIT_ANY_TIME, LIMIT_ANY_TIME);
        if (count > ((long) COUNT_ANY_TIME * LIMIT_ANY_TIME)) {
            COUNT_ANY_TIME++;
        }
        return ResponseData.success(SearchHot.getObject(hots));
    }
    public ResponseData labelHot(User user, String ip) {
//        log.info("[{}] labelHot userId:{} IP:{}", TimeUtil.getNowDate(), user.getId(), ip);
        List<SearchHot> month = hotDao.getByHot(TimeUtil.getMonthZero(),0, LIMIT_ANY_TIME);
        List<SearchHot> year = hotDao.getByHot(TimeUtil.getYearZero(),0, LIMIT_ANY_TIME);
        JSONObject object = ResponseData.object("month", SearchHot.getObject(month));
        object.put("year", SearchHot.getObject(year));
        return ResponseData.success(object);
    }
}
