package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONArray;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.*;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.ShowPayUtil;
import com.telebott.movie2java.util.TimeUtil;
import com.telebott.movie2java.util.ToolsUtil;
import com.telebott.movie2java.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ApiService {
    @Autowired
    private VideoDao videoDao;
    @Autowired
    private VideoClassDao videoClassDao;
    @Autowired
    private VideoPpvodDao videoPpvodDao;
    @Autowired
    private VideoPlayDao videoPlayDao;
    @Autowired
    private VideoLikeDao videoLikeDao;

    @Autowired
    private CashInConfigDao cashInConfigDao;
    @Autowired
    private CashInOrderDao cashInOrderDao;
    @Autowired
    private OrderService orderService;

    private String getVideoConfig(String name){
        List<VideoPpvod> ppvods = videoPpvodDao.findAllByName(name);
        if(ppvods.size() > 0){
            return ppvods.get(0).getVal();
        }
        return null;
    }
    private long getVideoConfigLong(String name){
        String val = getVideoConfig(name);
        if (val != null && ToolsUtil.isNumberString(val)){
            return Long.parseLong(val);
        }
        return 0;
    }
    private boolean getVideoConfigBool(String name){
        return getVideoConfigLong(name) > 0;
    }
    public ResponseData handlerYzm(YzmData yzmData, String passwd) {
        log.info("[{}]视频回调 {}", TimeUtil.getNowDate(), yzmData);
        String pass = getVideoConfig("passwd");
        if (!Objects.equals(pass, passwd)){
            return ResponseData.fail("auth failed");
        }
        if (StringUtils.isEmpty(yzmData.getShareid())) return ResponseData.fail("add failed");
        Video video = videoDao.findAllByShareId(yzmData.getShareid());
        if (video == null){
            video = new Video();
            video.setStatus(1);
            video.setShareId(yzmData.getShareid());
            video.setAddTime(System.currentTimeMillis());
            video.setLikes(ToolsUtil.cardinality(getVideoConfigLong("miniLikes"),getVideoConfigLong("maxLikes")));
            video.setPlays(ToolsUtil.cardinality(getVideoConfigLong("miniPlays"),getVideoConfigLong("maxPlays")));
        }
        video.setUpdateTime(System.currentTimeMillis());
        if (yzmData.getRpath().contains("/")){
            String[] rpath = yzmData.getRpath().split("/");
            for (int i = 0; i < rpath.length; i++) {
                rpath[i] = UrlUtil.encode(rpath[i]);
            }
            yzmData.setRpath(StringUtils.join(rpath,"/"));
        }else{
            yzmData.setRpath(UrlUtil.encode(yzmData.getRpath()));
        }
        if (yzmData.getPath().contains("/")){
            String[] path = yzmData.getPath().split("/");
            for (int i = 0; i < path.length; i++) {
                path[i] = UrlUtil.encode(path[i]);
            }
            yzmData.setPath(StringUtils.join(path,"/"));
        }else{
            yzmData.setPath(UrlUtil.encode(yzmData.getPath()));
        }
        String pic1 = yzmData.getRpath() + "/1.jpg";
        video.setTitle(yzmData.getTitle());
        video.setVodContent(video.getTitle());
        if (StringUtils.isNotEmpty(yzmData.getCategory())) {
            VideoClass videoClass = videoClassDao.findAllByName(yzmData.getCategory());
            if (videoClass == null) {
                videoClass = new VideoClass();
                videoClass.setName(yzmData.getCategory());
                videoClass.setAddTime(System.currentTimeMillis());
                videoClass.setUpdateTime(System.currentTimeMillis());
                videoClassDao.saveAndFlush(videoClass);
            }
//            System.out.println(videoClass.getId());
            video.setVodClass(videoClass.getId());
        }
        if (yzmData.getMetadata() != null) {
            video.setVodDuration(yzmData.getMetadata().getTime());
        }
        if (!yzmData.getDomain().endsWith("/")) yzmData.setDomain(yzmData.getDomain()+"/");
        if (yzmData.getOutput() != null) {
            String picDomain = yzmData.getDomain();
            if (StringUtils.isNotEmpty(yzmData.getPicdomain())) picDomain = yzmData.getPicdomain();
//            assert pic1 != null;
            if (!picDomain.endsWith("/") && !pic1.startsWith("/")) {
                picDomain = picDomain + "/";
            }
            video.setPicThumb(picDomain + pic1);

            if (yzmData.getOutput().getVideo() != null) {
                List<VideoData> videoDataList = yzmData.getOutput().getVideo();
                List<VideoPlayUrl> playUrls = new ArrayList<>();
                for (VideoData data : videoDataList) {
                    VideoPlayUrl playUrl = new VideoPlayUrl();
                    playUrl.setResolution(data.getResolution());
                    playUrl.setSize(data.getLength());
                    playUrl.setUrl(yzmData.getDomain() + yzmData.getRpath() + "/" + data.getBitrate() + "kb/hls/index.m3u8");
                    playUrls.add(playUrl);
                }
                video.setVodPlayUrl(playUrls.get(playUrls.size()-1).getUrl());
//                video.setVodPlayUrl(JSONArray.toJSONString(playUrls));
            }
        }
        videoDao.saveAndFlush(video);
        return ResponseData.success("add success");
    }

    public String handlerToPayNotify(ToPayNotify payNotify) {
        log.info("[{}]支付回调 {}", TimeUtil.getNowDate(), payNotify);
        if (StringUtils.isEmpty(payNotify.getMchid())) return "fail";
        List<CashInConfig> configs = cashInConfigDao.findAllByMchId(payNotify.getMchid());
        if (configs.size() == 0) return "fail";
        if (!ShowPayUtil.toPayNotify(payNotify,configs.get(0))) return "fail";
        CashInOrder order = cashInOrderDao.findAllByOrderNoAndStatus(payNotify.getOut_trade_no(),0);
        if (order == null) return "fail";
        order.setTotalFee(payNotify.getTotal_fee());
        if (!orderService.handlerToPayNotify(order)) return "fail";
        return "success";
    }
}
