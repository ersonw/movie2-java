package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.OssConfig;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.ShortVideoFile;
import com.telebott.movie2java.entity.*;
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

@Slf4j
@Service
public class ShortVideoService {
    private static final int MAX_TITLE_LENGTH = 100;

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
        video.setStatus(0);
        if(filePath.startsWith("http")){
            video.setPlayUrl(filePath);
        }
        if(imagePath.startsWith("http")){
            video.setPic(imagePath);
        }
        video.setFile(JSONObject.toJSONString(new ShortVideoFile(files)));
        List<ShortVideo> videoList = shortVideoDao.findAllByFileAndUserId(video.getFile(), user.getId());
        if(videoList.size() > 0){
            return ResponseData.error("重复上传，无效操作！");
        }
        shortVideoDao.saveAndFlush(video);
        return ResponseData.success(ResponseData.object("upload",true));
    }
    public String getOssUrl(String path,OssConfig config){
        String endPoint = config.getEndPoint();
        if(!endPoint.startsWith("http")){
            if(config.getUseSSL()){
                endPoint = "https://"+endPoint;
            }else {
                endPoint = "http://"+endPoint;
            }
        }
        if(config.getPort() != null){
            endPoint = endPoint+":"+config.getPort();
        }
//        log.error("endPoint:{} AccessKey:{} SecretKey:{}",endPoint,config.getAccessKey(),config.getSecretKey());
        switch (config.getType()){
            case OssConfig.TYPE_UPLOAD_OSS_MINIO:
                try {
                    MinioClient minioClient = new MinioClient(endPoint, config.getAccessKey(), config.getSecretKey());
//                    ObjectStat objectStat = minioClient.statObject(config.getBucket(), path);
//                    System.out.println(objectStat);
//                    System.out.printf(minioClient.getObjectUrl(config.getBucket(),path));
                    ObjectStat stat = minioClient.statObject(config.getBucket(),path);
//                    System.out.printf("length:%d\n",stat.length());
//                    if ()
                    return minioClient.getObjectUrl(config.getBucket(),path);
                } catch (InvalidPortException | InvalidEndpointException | InvalidBucketNameException |
                         InsufficientDataException | XmlPullParserException | ErrorResponseException |
                         NoSuchAlgorithmException | IOException | NoResponseException | InvalidKeyException |
                         InternalException e) {
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }
    public JSONObject getComment(ShortVideoComment comment, long userId){
        JSONObject o = new JSONObject();
        o.put("id", comment.getId());
        o.put("text", comment.getText());
        o.put("addTime", comment.getAddTime());
        o.put("userId", comment.getUserId());
        User user = userDao.findAllById(comment.getUserId());
        if (user != null) {
            o.put("avatar", user.getAvatar());
            o.put("nickname", user.getNickname());
        }
        o.put("likes", shortVideoCommentLikeDao.countAllByCommentId(comment.getId()));
        o.put("like", shortVideoCommentLikeDao.findAllByUserIdAndCommentId(userId,comment.getId()) != null);
        o.put("reply", getComments(comment.getVideoId(), userId, comment.getId()));
        return o;
    }
    public JSONArray getComments(long videoId, long userId){
        return getComments(videoId, userId,0);
    }
    public JSONArray getComments(long videoId, long userId, long replyId){
        List<ShortVideoComment> comments = shortVideoCommentDao.findAllByVideoIdAndReplyId(videoId,replyId);
        JSONArray jsonArray = new JSONArray();
        for (ShortVideoComment comment : comments) {
            jsonArray.add(getComment(comment, userId));
        }
        return jsonArray;
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

        object.put("likes",shortVideoLikeDao.countAllByVideoId(video.getId()));
        object.put("like",shortVideoLikeDao.findAllByUserIdAndVideoId(userId,video.getId()) != null);

        object.put("comments",getComments(video.getId(),userId));
        object.put("comment",shortVideoCommentDao.countAllByVideoId(video.getId()));
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
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,"id"));
        Page<ShortVideo> videoPage;
        if (id == 0){
            videoPage = shortVideoDao.getAllByForwards(user.getId(), pageable);
            if (videoPage.getContent().size() == 0){
                page = page - videoPage.getTotalPages();
                if (page < 0) page = 0;
                pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,"id"));
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
        object = ResponseData.object("total",arry.size());
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
        ShortVideoPlay play = shortVideoPlayDao.findAllByVideoIdAndUserIdAndAddTimeGreaterThanEqual(video.getId(), user.getId(),time);
        if (play == null){
            play = new ShortVideoPlay(video.getId(), user.getId(),ip);
            shortVideoPlayDao.saveAndFlush(play);
        }
        ShortVideoScale scale = shortVideoScaleDao.findAllByVideoIdAndUserIdAndAddTimeGreaterThanEqual(video.getId(), user.getId(),time);
        if (scale == null){
            scale = new ShortVideoScale(user.getId(), video.getId(),seek,ip);
        }
        scale.setVideoTime(seek);
        shortVideoScaleDao.saveAndFlush(scale);
        return ResponseData.success("");
    }

    public ResponseData concentration(int page, User user, String ip) {
        if(user == null) return ResponseData.error("");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, 10);
        Page<ShortVideo> videoPage = shortVideoDao.getAllVideos(user.getId(),pageable);
        if (videoPage.getContent().size() == 0){
            page = page - videoPage.getTotalPages();
            if (page < 0) page = 0;
            pageable = PageRequest.of(page, 10);
            videoPage = shortVideoDao.getAllVideos(pageable);
        }
        JSONObject object = ResponseData.object("total",videoPage.getTotalPages());
        JSONArray arry = new JSONArray();
        for (ShortVideo video : videoPage.getContent()) {
            JSONObject json = getShortVideo(video, user.getId());
            if (json != null) arry.add(json);
        }
        object = ResponseData.object("total",arry.size());
        object.put("list",arry);
        return ResponseData.success(object);
    }
}
