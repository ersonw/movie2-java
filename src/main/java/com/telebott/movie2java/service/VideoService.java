package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.TimeUtil;
import com.telebott.movie2java.util.ToolsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class VideoService {
    private static int VIDEO_ANY_TIME = 0;
    private static int VIDEO_AD = 0;
    private static final int MAX_COMMENT_WORD_LENGTH = 100;
    private static final int MINI_COMMENT_WORD_LENGTH = 2;

    private static final int SORT_BY_ALL = 0;
    private static final int SORT_BY_NEW = 1;
    private static final int SORT_BY_HOT = 2;
    private static final int SORT_BY_LIKE = 3;
    private static final int SORT_BY_COMMENT = 4;

    private static int VIDEO_CONCENTRATION_PAGE = 0;

    @Autowired
    private VideoProducedDao videoProducedDao;
    @Autowired
    private VideoClassDao videoClassDao;
    @Autowired
    private ApiService apiService;
    @Autowired
    private UserService userService;
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
    @Autowired
    private  VideoConcentrationListDao videoConcentrationListDao;
    @Autowired
    private VideoConcentrationDao videoConcentrationDao;

    @Autowired
    private VideoPublicityReportDao videoPublicityReportDao;
    @Autowired
    private VideoPublicityDao videoPublicityDao;
    @Autowired
    private UserBalanceDiamondDao userBalanceDiamondDao;
    @Autowired
    private ShortLinkService shortLinkService;

    public ResponseData categoryTags(User user, String ip) {
        List<VideoProduced> produceds = videoProducedDao.findAllByStatus(1);
        List<VideoClass> classes = videoClassDao.findAllByStatus(1);
        JSONObject object = ResponseData.object("produceds", getProduced(produceds));
        object.put("classes",getClass(classes));
        return ResponseData.success(object);
    }
    private JSONArray getProduced(List<VideoProduced> produceds){
        JSONArray array = new JSONArray();
        for (VideoProduced produced: produceds) {
            if (produced != null){
                JSONObject object = ResponseData.object("id", produced.getId());
                object.put("words",produced.getName());
                array.add(object);
            }
        }
        return array;
    }
    private JSONArray getClass(List<VideoClass> classes){
        JSONArray array = new JSONArray();
        for (VideoClass videoClass: classes) {
            if (videoClass != null){
                JSONObject object = ResponseData.object("id", videoClass.getId());
                object.put("words",videoClass.getName());
                array.add(object);
            }
        }
        return array;
    }
    public ResponseData player(long id, User user, String ip,boolean isWeb) {
        System.out.println(isWeb);
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
        VideoScale scale = videoScaleDao.findAllByUserIdAndVideoId(user.getId(),id);
        if (scale == null) {
            scale = new VideoScale();
            scale.setUserId(user.getId());
            scale.setAddTime(System.currentTimeMillis());
            scale.setVideoTime(0);
            scale.setUpdateTime(System.currentTimeMillis());
            scale.setVideoId(id);
        }
        if (scale.getVideoTime() >= (video.getVodDuration()-20)){
            scale.setVideoTime(0);
            scale.setUpdateTime(System.currentTimeMillis());
        }
//        System.out.printf((video.getVodDuration()-scale.getVideoTime())+"");
        videoScaleDao.saveAndFlush(scale);
        VideoPlay play = new VideoPlay();
        play.setAddTime(System.currentTimeMillis());
        play.setVideoId(id);
        play.setUserId(user.getId());
        play.setIp(ip);
        videoPlayDao.saveAndFlush(play);
        JSONObject object = new JSONObject();
        object.put("member", userService.isMembership(user.getId()));
        object.put("seek", scale.getVideoTime());
        object.put("price", 0);
        object.put("total", 0);
        object.put("trial", video.getTrial());
        if (video.getTrial() == 0){
            object.put("trial", apiService.getVideoConfigLong("VideoTrial"));
        }
        VideoPay pay = videoPayDao.findAllByVideoId(id);
        if (pay != null) {
            boolean isPay = videoPayRecordDao.findAllByUserIdAndPayId(user.getId(),pay.getId()) != null;
            object.put("pay", isPay);
            object.put("price", pay.getAmount());
            object.put("total", pay.getAmount());
            long percent = apiService.getVideoConfigLong("VideoPayLess");
            if(percent > 0) {
                object.put("total", pay.getAmount() - (pay.getAmount() / percent));
            }
        }else {
            if(checkVideoAccess(user.getId(), id)){
                object.put("pay", true);
            }else {
                object.put("pay", !apiService.getVideoConfigBool("VideoPay"));
            }
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
        VideoScale scale = videoScaleDao.findAllByUserIdAndVideoId(user.getId(),id);
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
    public boolean checkVideoAccess(long userId, long videoId){
        VideoPay pay = videoPayDao.findAllByVideoId(videoId);
        if (pay != null) {
            return videoPayRecordDao.findAllByUserIdAndPayId(userId,pay.getId()) != null;
        }
        return userService.isMembership(userId);
    }
    public ResponseData comment(long id, String text, long seek,
                                long toId , User user, String ip) {
        if (id == 0) return ResponseData.error("You can't find the video with id 0");
        Video video = videoDao.findAllById(id);
        if (video == null) return ResponseData.error("Video not found");
        if (user == null) return ResponseData.success(ResponseData.object("error", "login"));
        if(StringUtils.isEmpty(text)) return ResponseData.error();
        if(!checkVideoAccess(user.getId(),id)) return ResponseData.error("试看权限暂未开放评论哟～");
        if (text.length() < MINI_COMMENT_WORD_LENGTH) return ResponseData.error("评论或者回复不能少于"+MINI_COMMENT_WORD_LENGTH+"个字符");
        if (text.length() > MAX_COMMENT_WORD_LENGTH) return ResponseData.error("评论或者回复不能大于"+MAX_COMMENT_WORD_LENGTH+"个字符");
        if (ToolsUtil.filterCommentBlack(text)) return ResponseData.error("禁止发布敏感词语");
        VideoComment comment = videoCommentDao.findAllByUserIdAndVideoIdAndText(user.getId(), video.getId(),text);
//        if (comment != null) return ResponseData.error("此评论已经录入哦，请勿灌水，谢谢！");
        if (comment != null) return ResponseData.error("");
        comment = new VideoComment();
        comment.setStatus(new Long(apiService.getVideoConfigLong("commentAudit")).intValue());
        if(ToolsUtil.filterWords(text)){
            comment.setStatus(0);
        }
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
                    return ResponseData.error("未审核通过的评论暂时不可回复！");
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
        Page<VideoComment> videoComments = videoCommentDao.getAllByLike(0,id,1,pageable);
        commentList.addAll(videoComments.getContent());
        JSONObject object = ResponseData.object("total", videoComments.getTotalPages());
        object.put("list",getComment(commentList,user));
        return ResponseData.success(object);
    }
    public JSONArray getComment(List<VideoComment> videoComments, User _user){
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
                object.put("like", videoCommentLikeDao.findAllByUserIdAndCommentId(_user.getId(), comment.getId()) != null);
                object.put("reply", getComment(videoCommentDao.findAllByReplyId(comment.getId()),_user));
                array.add(object);
            }
        }
        return array;
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
            json.put("pay", true);
            json.put("price", pay.getAmount());
        }else {
            json.put("pay", apiService.getVideoConfigBool("VideoPay"));
        }
        return json;
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
            array.add(getVideo(video));
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
//        if(!checkVideoAccess(user.getId(),id)) return ResponseData.error("试看权限暂未开放点赞哟～");
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
        videoCommentDao.removeAllById(comment.getId());
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

    public ResponseData categoryList(int first, long second, long last,int page, User user, String ip) {
//        log.error("first:{} second:{} last:{} page:{}",first,second,last,page);
        if(first < 0) first = 0;
        if(second < 0) second = 0;
        if(last < 0) last = 0;
        page--;
        if(page < 0) page = 0;
        JSONObject json = new JSONObject();
        switch (first) {
            case SORT_BY_ALL:
            case SORT_BY_NEW:
                json = getSortByAll(second,last,page);
                break;
            case SORT_BY_HOT:
                json = getSortByHot(second,last,page);
                break;
        }
        return ResponseData.success(json);
    }
    private JSONObject getSortByHot(long second, long last, int page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Video> videoPage;
        if(second == 0 && last == 0){
            videoPage = videoDao.getVideoByStatus(1,pageable);
        }else if(last == 0){
            videoPage = videoDao.getVideoByProduced(second,pageable);
        }else if(second == 0){
            videoPage = videoDao.getVideoByVodClass(last,pageable);
        }else {
            videoPage = videoDao.getVideoByVodClassAndProduced(last,second,pageable);
        }
        return getVideoObject(videoPage);
    }
    private JSONObject getSortByAll(long second, long last, int page){
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Video> videoPage;
        if(second == 0 && last == 0){
            videoPage = videoDao.findAllByStatus(1,pageable);
        }else if(last == 0){
            videoPage = videoDao.getAllByProduced(second,pageable);
        }else if(second == 0){
            videoPage = videoDao.findAllByVodClassAndStatus(last,1,pageable);
        }else {
            videoPage = videoDao.getAllByVodClassAndProduced(last,second,pageable);
        }
        return getVideoObject(videoPage);
    }
    private JSONObject getVideoObject(Page<Video> videoPage) {
        JSONObject object = new JSONObject();
        object.put("total",videoPage.getTotalPages());
        JSONArray array = new JSONArray();
        for (Video v: videoPage.getContent()) {
            if(v != null) {
                array.add(getVideo(v));
            }
        }
        object.put("list",array);
        return object;
    }

    private Page<Video> getVideo(long concentrationId){
        Pageable pageable = PageRequest.of(VIDEO_CONCENTRATION_PAGE, 6, Sort.by(Sort.Direction.DESC, "id"));
        Page<Video> videoPage = videoDao.getVideoByConcentrations(concentrationId,pageable);
//        System.out.println(videoPage.getContent());
        if(videoPage.getTotalPages() > VIDEO_CONCENTRATION_PAGE){
            VIDEO_CONCENTRATION_PAGE++;
            return videoPage;
        }else if (videoPage.getTotalElements() > 0){
            VIDEO_CONCENTRATION_PAGE = 0;
            return getVideo(concentrationId);
        }else {
            return videoPage;
        }
    }
    public ResponseData concentrations(User user, String ip) {
        List<VideoConcentration> concentrations = videoConcentrationDao.findAllByList();
//        System.out.println(concentrations);
        JSONArray jsonArray= new JSONArray();
        for (VideoConcentration concentration: concentrations) {
            Page<Video> videoPage = getVideo(concentration.getId());
            JSONArray array = new JSONArray();
            for (Video video: videoPage.getContent()) {
                array.add(getVideo(video));
            }
            JSONObject json = ResponseData.object("videos", array);
            json.put("id", concentration.getId());
            json.put("name", concentration.getName());
            jsonArray.add(json);
        }
//        System.out.println(jsonArray);
        return ResponseData.success(ResponseData.object("list", jsonArray));
    }

    public ResponseData concentrationsAnytime(long id, User user, String ip) {
        if (id < 1) return ResponseData.error("id must be greater than 1");
        VideoConcentration concentration = videoConcentrationDao.findAllById(id);
        if (concentration == null) return ResponseData.error("concentration not found");
        Page<Video> videoPage = getVideo(id);
        JSONArray array = new JSONArray();
        for (Video video: videoPage.getContent()) {
            array.add(getVideo(video));
        }
        return ResponseData.success(ResponseData.object("list", array));
    }
    public ResponseData concentrations(long id,int page, User user, String ip) {
        if (id < 1) return ResponseData.error("id must be greater than 1");
        VideoConcentration concentration = videoConcentrationDao.findAllById(id);
        if (concentration == null) return ResponseData.error("concentration not found");
        page--;
        if(page < 0) page= 0;
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Video> videoPage = videoDao.getVideoByConcentrations(id,pageable);
        JSONArray array = new JSONArray();
        for (Video video: videoPage.getContent()) {
            array.add(getVideo(video));
        }
        return ResponseData.success(ResponseData.object("list", array));
    }

    public ResponseData membership(int page, User user, String ip) {
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, 10);
        Page<Video> videoPage = videoDao.getVideoByPay(pageable);
        return getResponseVideoList(videoPage);
    }

    public ResponseData diamond(int page, User user, String ip) {
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Video> videoPage = videoDao.getVideoByPay(1,pageable);
        return getResponseVideoList(videoPage);
    }

    private JSONArray getResponseVideoList(List<Video> videos) {
        JSONArray array = new JSONArray();
        for (Video video: videos) {
            array.add(getVideo(video));
        }
        return array;
    }
    private ResponseData getResponseVideoList(Page<Video> videoPage) {
        JSONObject object = ResponseData.object("list", getResponseVideoList(videoPage.getContent()));
        object.put("total", videoPage.getTotalPages());
        return ResponseData.success(object);
    }

    public ResponseData rank(int first,long second,User user, String ip) {
        Pageable pageable = PageRequest.of(0, 12);
        List<Video> videos = new ArrayList<>();
        long addTime = 0;
        if (first == 2) {
            addTime = TimeUtil.getMonthZero();
        }else if(first == 1) {
            addTime = TimeUtil.getYearZero();
        }
        if (second == 0) {
            Page<Video> videoPage = videoDao.getVideoByRank(addTime,pageable);
            videos = videoPage.getContent();
        }else if (second > 0) {
            Page<Video> videoPage = videoDao.getVideoByRank(addTime,second,pageable);
            videos = videoPage.getContent();
        }else {
            return ResponseData.error("Invalid video type: "+first);
        }
        return getRankVideoList(videos,addTime);
    }

    private ResponseData getRankVideoList(List<Video> videos, long addTime) {
        JSONArray array = new JSONArray();
        for (Video video: videos) {
            if(video != null) {
                JSONObject json = getVideo(video);
                json.put("plays", videoDao.getVideoByRank(addTime,video.getId()));
                array.add(json);
            }
        }
        return ResponseData.success(ResponseData.object("list", array));
    }
    public ResponseData publicity(User user, String ip) {
        if (user == null) return ResponseData.error("");
        List<VideoPublicity> publicities = videoPublicityDao.findAllByStatus(1);
        JSONArray array = new JSONArray();
        for (VideoPublicity publicity : publicities) {
            JSONObject json = new JSONObject();
            json.put("id", publicity.getId());
            json.put("image", publicity.getPic());
            json.put("url", publicity.getUrl());
            json.put("type", publicity.getType());
            array.add(json);
        }
        return ResponseData.success(ResponseData.object("list", array));
    }

    public ResponseData publicityReport(long id, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        if (user == null) return ResponseData.error("");
        VideoPublicity videoPublicity = videoPublicityDao.findAllById(id);
        if (videoPublicity == null) return ResponseData.error("");
        videoPublicityReportDao.save(new VideoPublicityReport(videoPublicity.getId(), user.getId(), ip));
        return ResponseData.success("");
    }

    public ResponseData buy(long id, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        if (user == null) return ResponseData.error("");
        Video video = videoDao.findAllById(id);
        if (video == null) return ResponseData.error("");
        VideoPay pay = videoPayDao.findAllByVideoId(id);
        if (pay == null) return ResponseData.error("视频无需购买!");
        VideoPayRecord record = videoPayRecordDao.findAllByUserIdAndPayId(user.getId(),pay.getId());
        if (record != null) return ResponseData.error("视频已经购买!");
        record = new VideoPayRecord(user.getId(),pay.getId(),ip);
        long balance = userBalanceDiamondDao.getAllByBalance(user.getId());
        if (balance < pay.getAmount()) return ResponseData.error("钻石余额不足!");
        UserBalanceDiamond diamond = new UserBalanceDiamond(user.getId(), -pay.getAmount(), "购买了视频【"+video.getTitle()+"】");
        userBalanceDiamondDao.save(diamond);
        videoPayRecordDao.save(record);
        return ResponseData.success("购买成功！",ResponseData.object("state",true));
    }
}
