package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.ToolsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class VideoService {
    private static int VIDEO_ANY_TIME = 0;
    private static int VIDEO_AD = 0;
    private static final int MAX_COMMENT_WORD_LENGTH = 200;
    private static final int MINI_COMMENT_WORD_LENGTH = 2;
    @Autowired
    private ApiService apiService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private PublicizeDao publicizeDao;
    @Autowired
    private VideoDao videoDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private VideoScaleDao videoScaleDao;
    @Autowired
    private VideoLikeDao videoLikeDao;
    @Autowired
    private VideoCommentDao videoCommentDao;
    @Autowired
    private VideoCommentLikeDao videoCommentLikeDao;
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
//        System.out.println(id+"=="+seek);
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
                                User user,
                                String ip) {
        if (id == 0) return ResponseData.error("You can't find the video with id 0");
        Video video = videoDao.findAllById(id);
        if (video == null) return ResponseData.error("Video not found");
        if (user == null) return ResponseData.success(ResponseData.object("error", "login"));
        if(StringUtils.isEmpty(text)) return ResponseData.error();
        if (text.length() < MINI_COMMENT_WORD_LENGTH) return ResponseData.error("评论或者回复不能少于"+MINI_COMMENT_WORD_LENGTH+"个字符");
        if (text.length() > MAX_COMMENT_WORD_LENGTH) return ResponseData.error("评论或者回复不能大于"+MAX_COMMENT_WORD_LENGTH+"个字符");
        if (ToolsUtil.filterWords(text)) return ResponseData.error("禁止发布敏感词语");
        VideoComment comment = videoCommentDao.findAllByUserIdAndVideoIdAndText(user.getId(), video.getId(),text);
        if (comment != null) return ResponseData.error("此评论已经录入哦，请勿灌水，谢谢！");
        comment = new VideoComment();
        comment.setStatus(new Long(apiService.getVideoConfigLong("commentAudit")).intValue());
        comment.setIp(ip);
        comment.setVideoId(id);
        comment.setVideoTime(seek);
        comment.setAddTime(System.currentTimeMillis());
        comment.setText(text);
        comment.setUserId(user.getId());
        if (toId > 0) {
            VideoComment videoComment = videoCommentDao.findAllById(toId);
            if (videoComment != null){
                if(videoComment.getStatus() == 0){
//                    return ResponseData.error("未审核通过的评论暂时不可回复！");
                }
                if (videoComment.getUserId() == user.getId()){
//                    return ResponseData.error("不能回复自己！");
                }
                comment.setReplyId(toId);
            }
        }
        videoCommentDao.saveAndFlush(comment);
        return ResponseData.success(ResponseData.object("state", "ok"));
    }
    public ResponseData comment(long id,int page, User user, String ip) {
        page--;
        if (page < 0) page =0;
        if (id == 0) return ResponseData.error("You can't find the video with id 0");
        Video video = videoDao.findAllById(id);
        if (video == null) return ResponseData.error("Video not found");
        if (user == null) return ResponseData.success(ResponseData.object("error", "login"));
//        //统计一级评论
//        //统计所有审核通过评论
//        long count = videoCommentDao.countAllByReplyIdAndVideoIdAndStatus(0,id, 0);
        //获取一级评论
        //先获取自己的评论
        List<VideoComment> commentList = videoCommentDao.findAllByReplyIdAndVideoIdAndUserIdAndStatus(0,id, user.getId(), 0);
        //获取所有审核通过评论
        Pageable pageable = PageRequest.of(page, 10);
        Page<VideoComment> videoComments = videoCommentDao.getAllByLike(1,pageable);
        commentList.addAll(videoComments.getContent());
        JSONObject object = ResponseData.object("total", videoComments.getTotalPages());
        object.put("list",getComment(commentList));
        return ResponseData.success(object);
    }
    public JSONArray getComment(List<VideoComment> videoComments){
        JSONArray array = new JSONArray();
        for (VideoComment comment: videoComments) {
            User user = userDao.findAllById(comment.getUserId());
            if (user != null){
                JSONObject object = ResponseData.object("id", comment.getId());
                object.put("status",comment.getStatus());
                object.put("text",comment.getText());
                object.put("addTime", comment.getAddTime());
                object.put("userId",comment.getUserId());
                object.put("avatar", user.getAvatar());
                object.put("nickname",user.getNickname());
                object.put("likes", videoCommentLikeDao.countAllByCommentId(comment.getId()));
                object.put("like", false);
                object.put("reply", getComment(videoCommentDao.findAllByReplyId(comment.getId())));
                array.add(object);
            }
        }
        return array;
    }
    public ResponseData anytime(User user, String ip) {
        Pageable pageable = PageRequest.of(VIDEO_ANY_TIME, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Video> videoPage = videoDao.findAllByStatus(1, pageable);
        if(VIDEO_ANY_TIME < videoPage.getTotalPages()){
            VIDEO_ANY_TIME++;
        }else {
            VIDEO_ANY_TIME = 0;
        }
        JSONArray array = new JSONArray();
        for (Video video : videoPage.getContent()) {
            array.add(searchService.getVideo(video));
        }
        JSONObject object = ResponseData.object("list",array);
        List<Publicize> publicizes = publicizeDao.findAllByPageAndStatus(1,1);

        if (VIDEO_AD < publicizes.size()) {
            object.put("swiper", getPublicize(publicizes.get(VIDEO_AD)));
            VIDEO_AD++;
        }else {
            VIDEO_AD = 0;
            object.put("swiper", getPublicize(publicizes.get(VIDEO_AD)));
        }
        return ResponseData.success(object);
    }
    public JSONObject getPublicize(Publicize publicize){
        JSONObject json = ResponseData.object("id",publicize.getId());
        json.put("image",publicize.getImage());
        json.put("url",publicize.getUrl());
        json.put("type",publicize.getType());
        return json;
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

    public ResponseData commentDelete(long id, User user, String ip) {
        if (user == null) return ResponseData.success(ResponseData.object("error", "login"));
        if (id < 1) return ResponseData.error("You can't find the comment with id 0");
        VideoComment comment = videoCommentDao.findAllById(id);
        if (comment == null ) return ResponseData.error("comment not fund ");
        if (comment.getUserId() != user.getId()) return ResponseData.error("该评论不能被删除");
        videoCommentDao.removeAllByToId(comment.getId());
        videoCommentLikeDao.removeAllByCommentId(comment.getId());
        videoCommentDao.delete(comment);
        return ResponseData.success(ResponseData.object("delete", true));
    }

    public ResponseData commentLike(long id, User user, String ip) {
        if (user == null) return ResponseData.success(ResponseData.object("error", "login"));
        if (id < 1) return ResponseData.error("You can't find the comment with id 0");
        VideoComment comment = videoCommentDao.findAllById(id);
        if (comment == null ) return ResponseData.error("comment not fund ");
        if (comment.getUserId() == user.getId()) return ResponseData.error("不能给自己点赞！");
        VideoCommentLike like = videoCommentLikeDao.findAllByCommentId(comment.getId());
        if (like == null){
            like = new VideoCommentLike(user.getId(), comment.getId(),ip);
            videoCommentLikeDao.save(like);
            return ResponseData.success(ResponseData.object("like", true));
        }
        videoCommentLikeDao.delete(like);
        return ResponseData.success(ResponseData.object("like", false));
    }
}
