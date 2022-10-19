package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.OssConfig;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.ShortVideoFile;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.ToolsUtil;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ShortVideoService {
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_COMMENT_WORD_LENGTH = 100;
    private static final int MINI_COMMENT_WORD_LENGTH = 2;

    @Autowired
    private ShortVideoDao shortVideoDao;
    @Autowired
    private ShortVideoCollectDao shortVideoCollectDao;
    @Autowired
    private ShortVideoForwardDao shortVideoForwardDao;
    @Autowired
    private ShortVideoCommentDao shortVideoCommentDao;
    @Autowired
    private ShortVideoCommentLikeDao shortVideoCommentLikeDao;
    @Autowired
    private ShortVideoLikeDao shortVideoLikeDao;
    @Autowired
    private ShortVideoPlayDao shortVideoPlayDao;
    @Autowired
    private ShortVideoDownloadDao shortVideoDownloadDao;
    @Autowired
    private ShortVideoPpvodDao shortVideoPpvodDao;
    @Autowired
    private ShortVideoScaleDao shortVideoScaleDao;
    @Autowired
    private ShortVideoShareDao shortVideoShareDao;
    @Autowired
    private ShortVideoCommentReportDao shortVideoCommentReportDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserFollowDao userFollowDao;
    public boolean getShortVideoConfigBool(String name){
        return getShortVideoConfigLong(name) > 0;
    }
    public long getShortVideoConfigLong(String name){
        String value = getShortVideoConfig(name);
        if(value == null) return 0;
        return Long.parseLong(value);
    }
    public String getShortVideoConfig(String name){
        List<ShortVideoPpvod> shortVideoPpvods = shortVideoPpvodDao.findAllByName(name);
        return shortVideoPpvods.isEmpty() ? null : shortVideoPpvods.get(0).getVal();
    }
    public ResponseData upload(String text, String filePath, String imagePath, long duration,String files, User user, String ip) {
//        System.out.printf(files);
//        return ResponseData.error("files must be specified");
        if (text != null && text.length() > MAX_TITLE_LENGTH) return  ResponseData.error("MAX_TITLE_LENGTH must be greater than title length");
        if (user == null) return ResponseData.error("user must be specified");
        if (imagePath == null) return ResponseData.error("imagePath must be specified");
        if (filePath == null) return ResponseData.error("filePath must be specified");
        if (files == null) return ResponseData.error("files must be specified");
        ShortVideo video = new ShortVideo();
        video.setAddTime(System.currentTimeMillis());
        video.setUpdateTime(System.currentTimeMillis());
        video.setTitle(text);
        video.setUserId(user.getId());
        video.setDuration(duration);
        video.setIp(ip);
        video.setStatus(1);
        if (getShortVideoConfigBool("auditUpload")){
            video.setStatus(0);
        }
        if (getShortVideoConfigBool("forward")){
            video.setForward(1);
        }
        if(filePath.startsWith("http")){
            video.setPlayUrl(filePath);
        }
        if(imagePath.startsWith("http")){
            video.setPic(imagePath);
        }
        video.setFile(JSONObject.toJSONString(new ShortVideoFile(files)));
        List<ShortVideo> videoList = shortVideoDao.findAllByFileAndUserId(video.getFile(), user.getId());
        if(videoList.size() > 0){
            return ResponseData.error("重复上传,无效操作！");
        }
        shortVideoDao.saveAndFlush(video);
        return ResponseData.success(ResponseData.object("upload",true));
    }
    public boolean isImage(String fileName){
        String[] extensions = "bmp,jpg,png,tif,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,WMF,webp,avif,apng".split(",");
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }else{
            return false;
        }
        for (String s: extensions) {
            if (s.equals(extension)) return true;
        }
        return false;
    }
    public String getOssUrl(String path,OssConfig config){
        String picDomian = getShortVideoConfig("picDomian");
        boolean force = getShortVideoConfigBool("force");
        if (force){
            config = getUploadConfig();
        }else if (config == null){
            config = getUploadConfig();
        }
        String endPoint = config.getEndPoint();
        if (!endPoint.startsWith("http")){
            if (config.getPort() != 443 && !config.getUseSSL()){
                if (config.getPort() != 80) endPoint = "http://" + endPoint + ":" + config.getPort();
            }else{
                endPoint = "https://" + endPoint;
                if (config.getPort() != 443) endPoint = endPoint + ":" +  config.getPort();
            }
        }
//        log.error("endPoint:{} AccessKey:{} SecretKey:{}",endPoint,config.getAccessKey(),config.getSecretKey());
        switch (config.getType()){
            case OssConfig.TYPE_UPLOAD_OSS_MINIO:
                try {
                    MinioClient minioClient = new MinioClient(endPoint, config.getAccessKey(), config.getSecretKey(),config.getUseSSL());
                    ObjectStat stat = minioClient.statObject(config.getBucket(),path);
                    String url = minioClient.getObjectUrl(config.getBucket(),path);
                    if (picDomian != null && isImage(path) && force) {
                        String[] urls = url.split(config.getEndPoint());
                        url = url.replaceAll(urls[0]+config.getEndPoint()+"/"+config.getBucket(), picDomian);
                    }
                    return url;
                } catch (InvalidPortException | InvalidEndpointException | InvalidBucketNameException |
                         InsufficientDataException | XmlPullParserException | ErrorResponseException |
                         NoSuchAlgorithmException | IOException | NoResponseException | InvalidKeyException |
                         InternalException e) {
                    log.error(e.getMessage());
                    log.error("minio domain {}", endPoint);
//                    e.printStackTrace();
//                    System.out.println(config);
                }
                break;
        }
        return null;
    }
    public JSONObject getShortVideo(ShortVideo video, long userId){
        if (video == null) return null;
        JSONObject object = new JSONObject();
        object.put("id",video.getId());
        object.put("title",video.getTitle());
        ShortVideoFile file = new ShortVideoFile(video.getFile());
        if(StringUtils.isNotEmpty(video.getPlayUrl())){
            object.put("playUrl",video.getPlayUrl());
        }else {
            String url = getOssUrl(file.getFilePath(),OssConfig.getOssConfig(file.getOssConfig()));
            if (url == null) return null;
            object.put("playUrl",url);
        }
        if (StringUtils.isNotEmpty(video.getPic())){
            object.put("pic",video.getPic());
        }else {
            String url = getOssUrl(file.getImagePath(),OssConfig.getOssConfig(file.getOssConfig()));
//            if (url != null) return null;
            object.put("pic",url);
        }
        object.put("addTime",video.getAddTime());

        object.put("plays",shortVideoPlayDao.countAllByVideoId(video.getId()));
        object.put("likes",shortVideoLikeDao.countAllByVideoId(video.getId()));
        object.put("like",shortVideoLikeDao.findAllByUserIdAndVideoId(userId,video.getId()) != null);
        object.put("pin",video.getPin() == 1);

//        object.put("comments",getComments(video.getId(),userId));
        object.put("comments",shortVideoCommentDao.countAllByVideoIdAndStatus(video.getId(),1));
        object.put("collects",shortVideoCollectDao.countAllByVideoId(video.getId()));
        object.put("follow", userFollowDao.findAllByUserIdAndToUserId(userId, video.getUserId()) != null);
        object.put("forwards", shortVideoShareDao.countAllByVideoId(video.getId()));
        object.put("forward", video.getForward() == 1);

        User user = userDao.findAllById(video.getUserId());
        if (user != null){
            object.put("avatar",user.getAvatar());
            object.put("nickname",user.getNickname());
        }
        object.put("userId",video.getUserId());
        return  object;
    }
    public ResponseData friend(long id,int page, User user, String ip) {
        if(user == null) return ResponseData.error("");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, 30, Sort.by(Sort.Direction.DESC,"id"));
        Page<ShortVideo> videoPage;
        if (id == 0){
            videoPage = shortVideoDao.getAllByForwards(user.getId(), pageable);
//            if (page > videoPage.getTotalPages()){
            if (videoPage.getContent().size() == 0){
                page = page - videoPage.getTotalPages();
                if (page < 0) page = 0;
                pageable = PageRequest.of(page, 30, Sort.by(Sort.Direction.DESC,"id"));
                videoPage = shortVideoDao.getAllByForward(user.getId(), pageable);
            }
        }else {
            videoPage = shortVideoDao.getAllByUser(user.getId(), pageable);
        }
        JSONObject object = ResponseData.object("total",videoPage.getTotalPages());
        JSONArray arry = new JSONArray();
        for (ShortVideo video : videoPage.getContent()) {
            JSONObject json = getShortVideo(video, user.getId());
            if (json != null) arry.add(json);
        }
//        object = ResponseData.object("total",arry.size());
        object.put("list",arry);
        return ResponseData.success(object);
    }

    public void test() {
        List<ShortVideo> shortVideos = shortVideoDao.findAll();
        ShortVideoFile file = new ShortVideoFile(shortVideos.get(0).getFile());
        getOssUrl(file.getFilePath(),OssConfig.getOssConfig(file.getOssConfig()));
    }

    public ResponseData heartbeat(long id, long seek, User user, String ip) {
        if (seek < 0) seek = 0;
        if (id < 1) return ResponseData.error("");
        if (user == null) return ResponseData.error("");
        ShortVideo video = shortVideoDao.findAllById(id);
        if (video == null) return ResponseData.error("");
        if (seek > (video.getDuration() - 6)) seek = 0;
        long time = System.currentTimeMillis() - (1000 * 60 * 30);
        List<ShortVideoPlay> plays = shortVideoPlayDao.findAllByVideoIdAndUserIdAndAddTimeGreaterThanEqual(video.getId(), user.getId(),time);
        if (plays.size() == 0){
            ShortVideoPlay play = new ShortVideoPlay(video.getId(), user.getId(),ip);
            shortVideoPlayDao.saveAndFlush(play);
        }
        List<ShortVideoScale> scales = shortVideoScaleDao.findAllByVideoIdAndUserIdAndAddTimeGreaterThanEqual(video.getId(), user.getId(),time);
        if (scales.size() == 0){
            ShortVideoScale scale = new ShortVideoScale(user.getId(), video.getId(),seek,ip);
            scales.add(scale);
        }
        scales.get(0).setVideoTime(seek);
        shortVideoScaleDao.saveAndFlush(scales.get(0));
        return ResponseData.success("");
    }

    public ResponseData concentration(int page, User user, String ip) {
        page--;
        if (page < 0) page = 0;
//        System.out.printf("page: %d\n", page);
        Pageable pageable = PageRequest.of(page, 30);
        Page<ShortVideo> videoPage;
        if(user == null) {
            videoPage = shortVideoDao.getAllVideos(pageable);
        }else {
            videoPage = shortVideoDao.getAllVideos(user.getId(),pageable);
//            System.out.println(videoPage.getContent().size());
//        if (page > videoPage.getTotalPages()){
            if (videoPage.getContent().size() == 0){
                page = page - videoPage.getTotalPages();
                if (page < 0) page = 0;
                pageable = PageRequest.of(page, 30);
                videoPage = shortVideoDao.getAllVideos(pageable);
            }
        }
        JSONObject object = ResponseData.object("total",videoPage.getTotalPages());
        JSONArray arry = new JSONArray();
//        System.out.println(videoPage.getContent());
        for (ShortVideo video : videoPage.getContent()) {
            JSONObject json = getShortVideo(video, user==null?0:user.getId());
            if (json != null) arry.add(json);
        }
//        object = ResponseData.object("total",arry.size());
        object.put("list",arry);
        return ResponseData.success(object);
    }

    public ResponseData like(long id, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        if (user == null) return ResponseData.error("");
        ShortVideoLike like = shortVideoLikeDao.findAllByUserIdAndVideoId(user.getId(), id);
        if (like!=null) return ResponseData.success("");
        like = new ShortVideoLike(user.getId(), id,ip);
        shortVideoLikeDao.saveAndFlush(like);
        return ResponseData.success(ResponseData.object("state", true));
    }
    public ResponseData unlike(long id, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        if (user == null) return ResponseData.error("");
        ShortVideoLike like = shortVideoLikeDao.findAllByUserIdAndVideoId(user.getId(), id);
        if (like==null) return ResponseData.success("");
        shortVideoLikeDao.delete(like);
        return ResponseData.success(ResponseData.object("state", true));
    }

    public ResponseData follow(long uid, User user, String ip) {
        if (uid < 1) return ResponseData.error("");
        if (user == null) return ResponseData.error("");
        if(uid == user.getId()) return ResponseData.success("不能自己关注自己哦");
        UserFollow follow = userFollowDao.findAllByUserIdAndToUserId(user.getId(), uid);
        if (follow!=null) return ResponseData.success("");
        follow = new UserFollow(user.getId(), uid,ip);
        userFollowDao.saveAndFlush(follow);
        return ResponseData.success(ResponseData.object("state", true));
    }
    public ResponseData unfollow(long uid, User user, String ip) {
        if (uid < 1) return ResponseData.error("");
        if (user == null) return ResponseData.error("");
        if(uid == user.getId()) return ResponseData.success("不能自己关注自己哦");
        UserFollow follow = userFollowDao.findAllByUserIdAndToUserId(user.getId(), uid);
        if (follow==null) return ResponseData.success("");
        userFollowDao.delete(follow);
        return ResponseData.success(ResponseData.object("state", true));
    }
    public JSONObject getComment(ShortVideoComment comment, long userId){
        JSONObject o = new JSONObject();
        o.put("id", comment.getId());
        o.put("text", comment.getText());
        o.put("addTime", comment.getAddTime());
        o.put("userId", comment.getUserId());
        o.put("pin", comment.getPin() == 1);
        User user = userDao.findAllById(comment.getUserId());
        if (user != null) {
            o.put("avatar", user.getAvatar());
            o.put("nickname", user.getNickname());
        }
        o.put("likes", shortVideoCommentLikeDao.countAllByCommentId(comment.getId()));
        o.put("like", shortVideoCommentLikeDao.findAllByUserIdAndCommentId(userId,comment.getId()).size() > 0);
        o.put("reply", shortVideoCommentDao.countAllByReply(comment.getId()));
        return o;
    }
    public JSONObject getCommentChild(ShortVideoComment comment, long userId, long ownerId){
        JSONObject o = new JSONObject();
        o.put("id", comment.getId());
        o.put("text", comment.getText());
        o.put("addTime", comment.getAddTime());
        o.put("userId", comment.getUserId());
        o.put("pin", comment.getPin() == 1);
        User user = userDao.findAllById(comment.getUserId());
        if (user != null) {
            o.put("avatar", user.getAvatar());
            o.put("nickname", user.getNickname());
        }
        o.put("likes", shortVideoCommentLikeDao.countAllByCommentId(comment.getId()));
        o.put("like", shortVideoCommentLikeDao.findAllByUserIdAndCommentId(userId,comment.getId()).size() > 0);
        if(comment.getReplyId() > 0 && comment.getReplyId() != ownerId){
            ShortVideoComment c = shortVideoCommentDao.findAllById(comment.getReplyId());
            if(c != null && c.getUserId() != comment.getUserId()){
                User u = userDao.findAllById(c.getUserId());
                if(u != null){
                    o.put("replyUser",u.getNickname());
                }
            }
        }
        return o;
    }
    public ResponseData comments(long id, int page, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        ShortVideo video = shortVideoDao.findAllById(id);
        if (video==null) return ResponseData.error("");
        page--;
        if (page < 0) page= 0;
        Pageable pageable = PageRequest.of(page, 12);
        Page<ShortVideoComment> commentPage = shortVideoCommentDao.getAllComments(id,pageable);

//        if (user == null){
//
//        }
        JSONObject json = ResponseData.object("total",commentPage.getTotalPages());
        json.put("count", shortVideoCommentDao.countAllByVideoIdAndStatus(id,1));
        JSONArray array = new JSONArray();
        for (ShortVideoComment comment : commentPage.getContent()){
            array.add(getComment(comment, user ==null?0:user.getId()));
        }
        json.put("list", array);
        return ResponseData.success(json);
    }

    public ResponseData comment(long id, String text, long toId, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        if (user == null) return ResponseData.error("用户未登录不可评论！");
        if (toId > 0){
//            User u = userDao.findAllById(toId);
//            if (u == null) return ResponseData.error("回复用户不存在！");
            ShortVideoComment comment = shortVideoCommentDao.findAllById(toId);
            if (comment == null|| comment.getStatus() != 1) return ResponseData.error("评论已被删除！");
        }else {
            toId = 0;
        }
        if (text.length() < MINI_COMMENT_WORD_LENGTH) return ResponseData.error("评论或者回复不能少于"+MINI_COMMENT_WORD_LENGTH+"个字符");
        if (text.length() > MAX_COMMENT_WORD_LENGTH) return ResponseData.error("评论或者回复不能大于"+MAX_COMMENT_WORD_LENGTH+"个字符");
        if (ToolsUtil.filterCommentBlack(text)) return ResponseData.error("禁止发布敏感词语");
        ShortVideo video = shortVideoDao.findAllById(id);
        if (video==null) return ResponseData.error("评论视频已被下架或者删除！");
        ShortVideoComment comment = new ShortVideoComment(toId, user.getId(), video.getId(),text,ip);
        if (getShortVideoConfigBool("auditComment")){
            comment.setStatus(0);
        }
        shortVideoCommentDao.saveAndFlush(comment);
        return ResponseData.success(getComment(comment, user.getId()));
    }

    public ResponseData commentChildren(long id, int page, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        ShortVideoComment comment = shortVideoCommentDao.findAllById(id);
        if (comment==null || comment.getStatus() != 1) return ResponseData.error("");
        page--;
        if (page < 0) page= 0;
        Pageable pageable = PageRequest.of(page, 3);
        Page<ShortVideoComment> commentPage = shortVideoCommentDao.getAllByReplyId(id,pageable);
        JSONObject json = ResponseData.object("total",commentPage.getTotalPages());
        json.put("count", shortVideoCommentDao.countAllByReplyIdAndStatus(id,1));
        JSONArray array = new JSONArray();
        for (ShortVideoComment c : commentPage.getContent()){
            array.add(getCommentChild(c, user!=null?user.getId():0, id));
        }
        json.put("list", array);
        return ResponseData.success(json);
    }

    public ResponseData commentLike(long id, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        if (user == null) return ResponseData.error("");
        List<ShortVideoCommentLike> likes = shortVideoCommentLikeDao.findAllByUserIdAndCommentId(user.getId(), id);
        if (likes.size()>0) return ResponseData.success("");
        ShortVideoCommentLike like = new ShortVideoCommentLike(user.getId(), id,ip);
        shortVideoCommentLikeDao.saveAndFlush(like);
        return ResponseData.success(ResponseData.object("state", true));
    }

    public ResponseData commentUnlike(long id, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        if (user == null) return ResponseData.error("");
        List<ShortVideoCommentLike> likes = shortVideoCommentLikeDao.findAllByUserIdAndCommentId(user.getId(), id);
        shortVideoCommentLikeDao.deleteAll(likes);
        return ResponseData.success(ResponseData.object("state", true));
    }

    public ResponseData commentDelete(long id, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        if(user == null) return ResponseData.error("");
        ShortVideoComment comment = shortVideoCommentDao.findAllById(id);
        if(comment == null) return ResponseData.error("评论已被删除！");
        ShortVideo video = shortVideoDao.findAllById(comment.getVideoId());
        if(video == null) return ResponseData.error("");
        if(comment.getUserId() != user.getId() && user.getId() != video.getUserId()) return ResponseData.error("");
        shortVideoCommentDao.deleteAllByLike(comment.getId());
        shortVideoCommentDao.deleteAllByReport(comment.getId());
        shortVideoCommentDao.deleteAllByComment(comment.getId());
        return ResponseData.success(ResponseData.object("state", true));
    }

    public ResponseData commentReport(long id, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        if(user == null) return ResponseData.error("");
        ShortVideoComment comment = shortVideoCommentDao.findAllById(id);
        if(comment == null || comment.getStatus() != 1) return ResponseData.error("评论已被删除！");
        long count = shortVideoCommentReportDao.countAllByCommentIdAndState(comment.getId(),0);
        if (count > 3){
            comment.setStatus(2);
            shortVideoCommentDao.save(comment);
        }
        shortVideoCommentReportDao.save(new ShortVideoCommentReport(comment.getId(),user.getId(),ip));
        return ResponseData.success(ResponseData.object("state",true));
    }

    public ResponseData commentPin(long id, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        if(user == null) return ResponseData.error("");
        ShortVideoComment comment = shortVideoCommentDao.findAllById(id);
        if(comment == null || comment.getStatus() != 1) return ResponseData.error("评论已被删除！");
        if(comment.getPin() == 1) return ResponseData.success("置顶成功！");
        comment.setPin(1);
        shortVideoCommentDao.save(comment);
        return ResponseData.success(ResponseData.object("state",true));
    }
    public ResponseData commentUnpin(long id, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        if(user == null) return ResponseData.error("");
        ShortVideoComment comment = shortVideoCommentDao.findAllById(id);
        if(comment == null || comment.getStatus() != 1) return ResponseData.error("评论已被删除！");
        if(comment.getPin() == 0) return ResponseData.success("取消置顶成功！");
        comment.setPin(0);
        shortVideoCommentDao.save(comment);
        return ResponseData.success(ResponseData.object("state",true));
    }

    public ResponseData uploadConfig(User instance, String ip) {
        OssConfig config = getUploadConfig();
        if (config == null) return ResponseData.error("获取配置失败！");
        return ResponseData.success(JSONObject.parseObject(JSONObject.toJSONString(config)));
    }
    public OssConfig getUploadConfig(){
        OssConfig config = new OssConfig();
        String conf = getShortVideoConfig("endPoint");
        if (conf == null)return null;
        config.setEndPoint(conf);
        conf = getShortVideoConfig("port");
        boolean useSSL = getShortVideoConfigBool("useSSL");
        config.setUseSSL(useSSL);
        if(conf == null){
            if (useSSL){
                conf = "443";
            }else {
                conf = "80";
            }
        }
        config.setPort(new Long(conf));
        conf = getShortVideoConfig("accessKey");
        if (conf == null)return null;
        config.setAccessKey(conf);
        conf = getShortVideoConfig("secretKey");
        if (conf == null)return null;
        config.setSecretKey(conf);
        conf = getShortVideoConfig("bucket");
        if (conf == null)return null;
        config.setBucket(conf);
        return config;
    }
}
