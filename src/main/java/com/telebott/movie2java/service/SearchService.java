package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    @Autowired
    private UserDao userDao;
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

    public ResponseData searchMovie(String text, int page, User user, String ip) {
        if (text == null || "".equals(text)) {
            return ResponseData.error("Invalid search text");
        }
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, 30, Sort.by(Sort.Direction.DESC, "id"));
        Page<Video> videoPage = videoDao.findAllByTitleLikeAndStatus(text,1, pageable);
        JSONArray array = new JSONArray();
        for (Video video : videoPage.getContent()) {
            array.add(getVideo(video));
        }
        if (user != null && page == 0){
            SearchHot hot = new SearchHot();
            hot.setAddTime(System.currentTimeMillis());
            hot.setUserId(user.getId());
            hot.setIp(ip);
            hot.setWords(text);
            hotDao.saveAndFlush(hot);
        }
        return ResponseData.success(ResponseData.object("list", array));
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
}
