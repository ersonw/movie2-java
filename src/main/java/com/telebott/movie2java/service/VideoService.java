package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VideoService {
    private static int VIDEO_ANY_TIME = 0;
    @Autowired
    private ApiService apiService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private VideoDao videoDao;
    @Autowired
    private VideoScaleDao videoScaleDao;
    @Autowired
    private VideoLikeDao videoLikeDao;
    @Autowired
    private VideoCommentDao videoCommentDao;
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
        }
        if (scale.getVideoTime() >= video.getVodDuration()){
            scale.setVideoTime(0);
            scale.setUpdateTime(System.currentTimeMillis());
        }
        videoScaleDao.saveAndFlush(scale);
        VideoPlay play = new VideoPlay();
        play.setAddTime(System.currentTimeMillis());
        play.setVideoId(id);
        play.setUserId(user.getId());
        play.setIp(ip);
        videoPlayDao.saveAndFlush(play);
        JSONObject object = new JSONObject();

        object.put("seek", scale.getVideoTime());
        object.put("price", 0);
        object.put("trial", video.getTrial());
        if (video.getTrial() == 0){
            object.put("trial", apiService.getVideoConfigLong("VideoTrial"));
        }
        VideoPay pay = videoPayDao.findAllByVideoId(id);
        if (pay != null) {
            object.put("pay", videoPayRecordDao.findAllByUserId(user.getId()) != null);
            object.put("price", pay.getAmount());
        }else {
            object.put("pay", !apiService.getVideoConfigBool("VideoPay"));
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
        System.out.println(id+"=="+seek);
        if (user == null) {
            return ResponseData.error();
        }
        if (id == 0) {
            return ResponseData.error();
        }
        if (seek < 0){
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
        if (scale.getVideoTime() == seek){
            return ResponseData.error();
        }
        scale.setVideoTime(seek);
        videoScaleDao.saveAndFlush(scale);
        scale.setUpdateTime(System.currentTimeMillis());
        return ResponseData.error();
    }

    public ResponseData comment(long id,
                                String text,
                                long seek,
                                long toId ,
                                User instance,
                                String ip) {
        if (id == 0) return ResponseData.error("You can't find the video with id 0");
        Video video = videoDao.findAllById(id);
        if (video == null) return ResponseData.error("Video not found");
        return ResponseData.success();
    }
    public ResponseData comment(long id,int page, User user, String ip) {
        if (id == 0) return ResponseData.error("You can't find the video with id 0");
        Video video = videoDao.findAllById(id);
        if (video == null) return ResponseData.error("Video not found");
        return ResponseData.success();
    }

    public ResponseData anytime(User user, String ip) {
        Pageable pageable = PageRequest.of(VIDEO_ANY_TIME, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Video> videoPage = videoDao.findAllByStatus(1, pageable);
        JSONArray array = new JSONArray();
        for (Video video : videoPage.getContent()) {
            array.add(searchService.getVideo(video));
        }
        JSONObject object = ResponseData.object("list",array);
        return ResponseData.success();
    }

    public ResponseData like(long id, User user, String ip) {
        if (id == 0) return ResponseData.error("You can't find the video with id 0");
        Video video = videoDao.findAllById(id);
        if (video == null) return ResponseData.error("Video not found");
        if (user == null) return ResponseData.error();
        VideoLike like = videoLikeDao.findAllByUserIdAndVideoId(user.getId(),id);
        JSONObject object = ResponseData.object("like",false);
        if (like == null){
            like = new VideoLike(user.getId(), id,ip);
            videoLikeDao.saveAndFlush(like);
            object = ResponseData.object("like",true);
        }else {
            videoLikeDao.delete(like);
        }
        return ResponseData.success(object);
    }

    public ResponseData share(long id, User user, String ip) {
        if (id == 0) return ResponseData.error("You can't find the video with id 0");
        Video video = videoDao.findAllById(id);
        if (video == null) return ResponseData.error("Video not found");
        if (user == null) return ResponseData.error();
        JSONObject object = ResponseData.object("id",video.getId());
        object.put("title",video.getTitle());
        object.put("vodContent",video.getVodContent());
        object.put("picThumb",video.getPicThumb());
        object.put("shareUrl","");
        return ResponseData.success(object);
    }
}
