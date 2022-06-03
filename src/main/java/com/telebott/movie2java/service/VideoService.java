package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VideoService {
    @Autowired
    private VideoDao videoDao;
    @Autowired
    private VideoScaleDao videoScaleDao;
    @Autowired
    private VideoLikeDao videoLikeDao;
    @Autowired
    private VideoPlayDao videoPlayDao;
    @Autowired
    private VideoPayDao videoPayDao;
    @Autowired
    private VideoPayRecordDao videoPayRecordDao;

    public ResponseData player(long id, User user, String ip) {
//        System.out.println(user);
        if (user == null) {
            return ResponseData.success(ResponseData.object("error", "login"));
        }
        if (id == 0) {
            return ResponseData.error("You can't find the video with id 0");
        }
        Video video = videoDao.findAllById(id);
        if (video == null) {
            return ResponseData.error("Video not found");
        }
        VideoScale scale = videoScaleDao.findAllByUserId(user.getId());
        if (scale == null) {
            scale = new VideoScale();
            scale.setUserId(user.getId());
            scale.setAddTime(System.currentTimeMillis());
            scale.setVideoTime(0);
            scale.setUpdateTime(System.currentTimeMillis());
            scale.setVideoId(id);
            videoScaleDao.saveAndFlush(scale);
        }
        VideoPlay play = new VideoPlay();
        play.setAddTime(System.currentTimeMillis());
        play.setVideoId(id);
        play.setUserId(user.getId());
        play.setIp(ip);
        videoPlayDao.saveAndFlush(play);
        JSONObject object = new JSONObject();
        object.put("pay", true);
        object.put("price", 0);
        VideoPay pay = videoPayDao.findAllByVideoId(id);
        if (pay != null) {
            object.put("pay", videoPayRecordDao.findAllByUserId(user.getId()) != null);
            object.put("price", pay.getAmount());
        }
        object.put("id", id);
        object.put("picThumb",video.getPicThumb());
        object.put("vodPlayUrl",video.getVodPlayUrl());
        object.put("title", video.getTitle());
        object.put("addTime", video.getAddTime());
        object.put("vodContent", video.getVodContent());
        object.put("like", videoLikeDao.findAllByUserIdAndVideoId(user.getId(), id) != null);
        object.put("likes", video.getLikes()+ videoLikeDao.countAllByVideoId(id));
        object.put("plays", video.getPlays()+ videoPlayDao.countAllByVideoId(id));
        return ResponseData.success(ResponseData.object("player", object));
    }

    public ResponseData heartbeat(long id, long seek, User user, String ip) {
        if (user == null) {
            return ResponseData.error();
        }
        if (id == 0) {
            return ResponseData.error();
        }
        Video video = videoDao.findAllById(id);
        if (video == null) {
            return ResponseData.error();
        }
        VideoScale scale = videoScaleDao.findAllByUserId(user.getId());
        if (scale == null) {
            scale = new VideoScale();
            scale.setUserId(user.getId());
            scale.setAddTime(System.currentTimeMillis());
            scale.setVideoId(id);
        }
        scale.setVideoTime(seek);
        videoScaleDao.saveAndFlush(scale);
        scale.setUpdateTime(System.currentTimeMillis());
        return ResponseData.error();
    }
}
