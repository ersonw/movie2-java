package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.TimeUtil;
import com.telebott.movie2java.util.WaLiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GameService {
    private static int INDEX_OF_SCROLL = 0;
    private static int MINI_OF_SCROLL = 12;
    @Autowired
    private UserDao userDao;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private GameButtonDao gameButtonDao;
    @Autowired
    private GameCarouselDao gameCarouselDao;
    @Autowired
    private GameConfigDao gameConfigDao;
    @Autowired
    private GameFundsDao gameFundsDao;
    @Autowired
    private GameOrderDao gameOrderDao;
    @Autowired
    private GameScrollDao gameScrollDao;
    @Autowired
    private GameWaterDao gameWaterDao;
    @Autowired
    private GamePublicityDao gamePublicityDao;
    @Autowired
    private GamePublicityReportDao gamePublicityReportDao;

    public boolean getConfigBool(String name) {
        return getConfigLong(name) > 0;
    }

    public long getConfigLong(String name) {
        String value = getConfig(name);
        if (value == null) return 0;
        return Long.parseLong(value);
    }

    public String getConfig(String name) {
        List<GameConfig> configs = gameConfigDao.findAllByName(name);
        return configs.isEmpty() ? null : configs.get(0).getVal();
    }

    public JSONArray getScroll(List<GameScroll> scrolls) {
        JSONArray array = new JSONArray();
        for (GameScroll scroll : scrolls) {
            JSONObject json = new JSONObject();
            json.put("text2", scroll.getName());
            json.put("text3", "在游戏【" + scroll.getGame() + "】中赢得了");
            json.put("text4", String.format("%.2f", scroll.getAmount() / 100D));
            json.put("text5", "元");
            array.add(json);
        }
        return array;
    }

    public ResponseData scroll(User user, String ip) {
        List<GameScroll> scrolls = new ArrayList<>();
        int total = 0;
        if (user != null) {
            Pageable pageable = PageRequest.of(INDEX_OF_SCROLL, MINI_OF_SCROLL);
            Page<GameScroll> scrollPage = gameScrollDao.findAllByAddTimeGreaterThanEqual(TimeUtil.getTodayZero(), pageable);
            if (scrollPage.getContent().size() > 0) {
                scrolls.addAll(scrollPage.getContent());
                total += scrollPage.getTotalPages();
            }

            if (scrollPage.getContent().size() < MINI_OF_SCROLL) {
                int page = INDEX_OF_SCROLL - (scrollPage.getTotalPages());
                int limit = MINI_OF_SCROLL - scrollPage.getContent().size();
                if (page < 0) page = 0;
                if (limit < 1) limit = 1;
                pageable = PageRequest.of(page, limit);
                scrollPage = gameScrollDao.findAll(pageable);
                if (scrollPage.getContent().size() > 0) {
                    scrolls.addAll(scrollPage.getContent());
                    total += scrollPage.getTotalPages();
                }
//                System.out.printf("total:%d elent:%d\n",scrollPage.getTotalPages(),scrollPage.getTotalElements());
//                System.out.printf("page:%d limit:%d\n",page,limit);
            }
        }
//        JSONObject json = ResponseData.object("total",total);
//        json.put("list", getScroll(scrolls));
//        return ResponseData.success(json);
        return ResponseData.success(ResponseData.object("list", getScroll(scrolls)));
    }

    public ResponseData getBalance(User user, String ip) {
        if (user == null) return ResponseData.error("");
        return ResponseData.success(ResponseData.object("balance", WaLiUtil.getBalance(user.getId())));
    }

    public ResponseData publicity(User user, String ip) {
        if (user == null) return ResponseData.error("");
        List<GamePublicity> publicities = gamePublicityDao.findAllByStatus(1);
        JSONArray array = new JSONArray();
        for (GamePublicity publicity : publicities) {
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
        GamePublicity gamePublicity = gamePublicityDao.findAllById(id);
        if (gamePublicity == null) return ResponseData.error("");
        gamePublicityReportDao.save(new GamePublicityReport(gamePublicity.getId(), user.getId(), ip));
        return ResponseData.success("");
    }

    public ResponseData enterGame(long id, User user, String ip) {
        if (id < 0) return ResponseData.error("");
        if (user == null) return ResponseData.error("");
        Game game = gameDao.findAllById(id);
        if (game == null) return ResponseData.error("");
        if(game.getStatus() != 1) return ResponseData.error("游戏【"+game.getName()+"】已被下架，暂不能游玩!");
//        String url = WaLiUtil.enterGame(user.getId(), game.getGameId());
        WaLiUtil.enterGame(user.getId(), game.getGameId());
        return ResponseData.success("");
    }

    public ResponseData list(User user, String ip) {
        if (user == null) return ResponseData.error("");
        List<Game> games = gameDao.findAllByStatus(1);
        JSONArray array = new JSONArray();
        for (Game game : games) {
            JSONObject object = new JSONObject();
            object.put("name", game.getName());
            object.put("image", getImageSmall(game));
            object.put("id", game.getId());
            array.add(object);
        }
        return ResponseData.success(ResponseData.object("list", array));
    }
    public String getImageSmall(Game game) {
//        if (StringUtils.isEmpty(game.getImage())) return game.getImage();
        if (StringUtils.isNotEmpty(game.getImage()) && game.getImage().startsWith("http")) return game.getImage();
        return getConfig("ImageDomain") + "/game/gameicon-200x200/200-200-0." + game.getGameId() + "." + game.getName() + ".png";
    }
    public String getImage(Game game) {
//        if (StringUtils.isEmpty(game.getImage())) return game.getImage();
        if (StringUtils.isNotEmpty(game.getImage()) && game.getImage().startsWith("http")) return game.getImage();
        return getConfig("ImageDomain") + "/game/gameicon-600400-square/600-400-0." + game.getGameId() + "." + game.getName() + ".png";
    }
}
