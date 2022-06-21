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
import org.springframework.beans.factory.annotation.Autowired;
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
    public JSONObject getShortVideo(ShortVideo video){
        if (video == null) return null;
        JSONObject object = new JSONObject();
        object.put("id",video.getId());
        object.put("pic",video.getPic());
        object.put("playUrl",video.getPlayUrl());
        object.put("title",video.getTitle());
        return  object;
    }
    public ResponseData friend(long id,int page, User user, String ip) {
        JSONArray arry = new JSONArray();
        return ResponseData.success(ResponseData.object("list",arry));
    }

    public void test() {
        List<ShortVideo> shortVideos = shortVideoDao.findAll();
        ShortVideoFile file = new ShortVideoFile(shortVideos.get(0).getFile());
        getOssUrl(file.getFilePath(),OssConfig.getOssConfig(file.getOssConfig()));
    }
}
