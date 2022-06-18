package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.ShortVideoDao;
import com.telebott.movie2java.data.OssConfig;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.ShortVideoFile;
import com.telebott.movie2java.entity.ShortVideo;
import com.telebott.movie2java.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ShortVideoService {
    private static final int MAX_TITLE_LENGTH = 100;

    @Autowired
    private ShortVideoDao shortVideoDao;
    public ResponseData upload(String text, String filePath, String imagePath, long duration,String files, User user, String ip) {
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
        return null;
    }
}
