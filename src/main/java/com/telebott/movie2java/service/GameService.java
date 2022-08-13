package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.EPayData;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.EPayUtil;
import com.telebott.movie2java.util.TimeUtil;
import com.telebott.movie2java.util.UrlUtil;
import com.telebott.movie2java.util.WaLiUtil;
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
public class GameService {
    private static int INDEX_OF_SCROLL = 0;
    private static int MINI_OF_SCROLL = 12;
    private static int INDEX_OF_CASH_IN = 0;
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
    @Autowired
    private CashInOrderDao cashInOrderDao;
    @Autowired
    private CashInConfigDao cashInConfigDao;
    @Autowired
    private CashInOptionDao cashInOptionDao;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private GameOutConfigDao gameOutConfigDao;
    @Autowired
    private GameOutCardDao gameOutCardDao;
    @Autowired
    private GameOutOrderDao gameOutOrderDao;

    public boolean getOutConfigBool(String name) {
        return getOutConfigLong(name) > 0;
    }

    public long getOutConfigLong(String name) {
        String value = getOutConfig(name);
        if (value == null) return 0;
        return Long.parseLong(value);
    }
    public double getOutConfigDouble(String name) {
        String value = getOutConfig(name);
        if (value == null) return 0D;
        return Double.parseDouble(value);
    }

    public String getOutConfig(String name) {
        List<GameOutConfig> configs = gameOutConfigDao.findAllByName(name);
        return configs.isEmpty() ? null : configs.get(0).getVal();
    }
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
            if(scroll.getGame().contains("提现")){
                json.put("text3", "兴高采烈地提走了一桶金 ");
            }else {
                json.put("text3", "在游戏【" + scroll.getGame() + "】中赢得了");
            }
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
            Pageable pageable = PageRequest.of(INDEX_OF_SCROLL, MINI_OF_SCROLL, Sort.by(Sort.Direction.DESC,"id"));
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
                pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC,"id"));
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
        if(id == 0){
            return ResponseData.success(WaLiUtil.enterGame(user.getId(), 0));
        }else {
            Game game = gameDao.findAllById(id);
            if (game == null) return ResponseData.error("");
            if(game.getStatus() != 1) return ResponseData.error("游戏【"+game.getName()+"】已被下架，暂不能游玩!");
            return ResponseData.success(WaLiUtil.enterGame(user.getId(), game.getGameId()));
        }
    }
    public JSONObject getGame(Game game) {
        return getGame(game, true);
    }
    public JSONObject getGame(Game game, boolean small) {
        JSONObject object = new JSONObject();
        object.put("name", game.getName());
        if (small){
            object.put("image", getImageSmall(game));
        }else {
            object.put("image", getImage(game));
        }
        object.put("id", game.getId());
        return object;
    }
    public ResponseData list(User user, String ip) {
        if (user == null) return ResponseData.error("");
        List<Game> games = gameDao.findAllByStatus(1);
        JSONArray array = new JSONArray();
        for (Game game : games) {
            array.add(getGame(game));
        }
        return ResponseData.success(array);
    }
    public String getImageSmall(Game game) {
//        if (StringUtils.isEmpty(game.getImage())) return game.getImage();
        if (StringUtils.isNotEmpty(game.getImage()) && game.getImage().startsWith("http")) return game.getImage();
        return getConfig("ImageDomain") + "/game/gameicon-200x200/200-200-" + game.getGameId() + "." + UrlUtil.encode(game.getName()) + ".png";
    }
    public String getImage(Game game) {
//        if (StringUtils.isEmpty(game.getImage())) return game.getImage();
        if (StringUtils.isNotEmpty(game.getImage()) && game.getImage().startsWith("http")) return game.getImage();
        return getConfig("ImageDomain") + "/game/gameicon-600400-square/600-400-" + game.getGameId() + "." + UrlUtil.encode(game.getName()) + ".png";
    }

    public ResponseData records(User user, String ip) {
        if (user == null) return ResponseData.error("");
        List<GameWater> waters = gameWaterDao.getAllByUser(user.getId());
        JSONArray array = new JSONArray();
        for (GameWater w: waters) {
            Game game = gameDao.findAllById(w.getGameId());
            if (game != null){
                array.add(getGame(game,false));
            }
        }
        return ResponseData.success(array);
    }

    public ResponseData test(User user, String ip) {
        if (user == null) return ResponseData.error("");
        if(!WaLiUtil.tranfer(user.getId(), 10000)) return ResponseData.error("上分失败");
        return ResponseData.success("上分成功");
    }

    public ResponseData buttons(User user, String ip) {
        if (user == null) return ResponseData.error("");
        List<GameButton> buttons = gameButtonDao.getAllButtons();
        JSONArray array = new JSONArray();
        for (GameButton b: buttons) {
            JSONObject object = new JSONObject();
            object.put("id", b.getId());
            object.put("amount", b.getAmount());
//            object.put("price", String.format("%.2f",b.getPrice() / 100D));
            object.put("price", b.getPrice());
            object.put("less", b.getLess() == 1);
            array.add(object);
        }
        return ResponseData.success(array);
    }
    public ResponseData button(long id, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if (id < 1) return ResponseData.error("");
        GameButton button = gameButtonDao.findAllById(id);
        if (button == null) return ResponseData.error("按钮已被禁用，请刷新重试！");
        List<CashInOption> options = new ArrayList<>();
        if(button.getCashInId() > 0){
            CashInConfig config = cashInConfigDao.findAllById(button.getCashInId());
            if(config != null && config.getStatus() == 1) options = getAllowed(config);
        }else {
            List<CashInConfig> configs = cashInConfigDao.findAllByStatus(1);
//            System.out.printf("length:%d",configs.size());
            if (configs.size() > 0){
                if(INDEX_OF_CASH_IN >= configs.size()){
                    INDEX_OF_CASH_IN = 0;
                }
                options = getAllowed(configs.get(INDEX_OF_CASH_IN));
                INDEX_OF_CASH_IN++;
            }else {
                options = new ArrayList<>();
            }
        }
        JSONArray array = new JSONArray();
        for (CashInOption option : options) {
            JSONObject object = new JSONObject();
            object.put("id", option.getId());
            object.put("name", option.getName());
            object.put("icon", option.getIcon());
            array.add(object);
        }
        return ResponseData.success(array);
    }
    public List<CashInOption> getAllowed(CashInConfig config){
        List<CashInOption> options = cashInOptionDao.findAllByStatus(1);
        if (config != null && StringUtils.isNotEmpty(config.getAllowed())){
            String[] allowed = config.getAllowed().split(",");
            options = new ArrayList<>();
            for (String s : allowed) {
                List<CashInOption> o = cashInOptionDao.findAllByStatusAndName(1, s);
//                    List<CashInOption> o = cashInOptionDao.findAllByStatusAndCode(1, s);
                if (o.size() > 0) {
                    options.add(o.get(0));
                }
            }
        }
        return options;
    }

    public ResponseData payment(long id, long toId, String schema, String serverName, int serverPort, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if (id < 1) return ResponseData.error("");
        if (toId < 1) return ResponseData.error("");
        GameButton button = gameButtonDao.findAllById(id);
        if (button == null || button.getStatus() != 1) return ResponseData.error("按钮已被禁用，请刷新重试！");
        CashInOption option = cashInOptionDao.findAllById(toId);
        if (option == null) return ResponseData.error("支付方式不可用，请刷新重试！");
        CashInConfig config = null;
        if(button.getCashInId() > 0){
            config = cashInConfigDao.findAllById(button.getCashInId());
            if (config == null || config.getStatus() != 1) {
                return ResponseData.error("通道已被禁用，请刷新重试！");
            }
        }else {
            List<CashInConfig> configs = cashInConfigDao.findAllByAllowedLikeAndStatus("%"+option.getName()+"%",1);
            if (configs.size() > 0) {
                config = configs.get(0);
            }
        }
        if (config == null) return ResponseData.error("支付方式不可用，请刷新重试！");
        GameOrder order = new GameOrder();
        order.setUserId(user.getId());
        order.setOrderNo(TimeUtil._getTime(0));
        order.setAmount(button.getAmount());
        order.setPrice(button.getPrice() * 100);
        order.setAddTime(System.currentTimeMillis());

        CashInOrder cashInOrder = new CashInOrder();
        cashInOrder.setType(option.getId());
        cashInOrder.setOrderNo(order.getOrderNo());
        cashInOrder.setOrderType(EPayUtil.GAME_ORDER);
        cashInOrder.setAddTime(System.currentTimeMillis());
        cashInOrder.setUpdateTime(System.currentTimeMillis());
        cashInOrder.setIp(ip);

        EPayData data = new EPayData();
        data.setMoney(String.format("%.2f",order.getPrice() / 100D));
        data.setPid(config.getMchId());
        data.setType(option.getCode());
        data.setOut_trade_no(order.getOrderNo());
        data.setNotify_url(config.getNotifyUrl());
        data.setReturn_url(config.getCallbackUrl());
        data.setSign(data.getSign(config.getSecretKey()));
        data.setUrl(config.getDomain());
        StringBuilder sb = new StringBuilder(schema).append("://").append(serverName);
        if (serverPort != 80 && serverPort != 443){
            sb.append(":").append(serverPort);
        }
        sb.append("/api/payment/").append(data.getOut_trade_no());
        authDao.pushOrder(data);
        gameOrderDao.saveAndFlush(order);
        cashInOrderDao.saveAndFlush(cashInOrder);
//        System.out.printf("%s\n",sb.toString());
        return ResponseData.success(ResponseData.object("url",sb.toString()));
    }
    public boolean handlerOrder(String orderId){
        GameOrder order = gameOrderDao.findAllByOrderNo(orderId);
        if (order == null) return false;
        User user = userDao.findAllById(order.getUserId());
        if (user == null) return false;
        GameFunds fund = new GameFunds(user.getId(), order.getAmount() * 100, "在线充值");
        if (WaLiUtil.tranfer(user.getId(),fund.getAmount())){
            gameFundsDao.saveAndFlush(fund);
            return true;
        }
        return false;
    }

    public ResponseData order(int page, User user, String ip) {
        if (user == null) return ResponseData.error("");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page,12,Sort.by(Sort.Direction.DESC,"id"));
        Page<CashInOrder> orderPage = cashInOrderDao.getAllByGame(user.getId(),pageable);
        JSONArray array = new JSONArray();
        for (CashInOrder order : orderPage.getContent()){
            GameOrder gameOrder = gameOrderDao.findAllByOrderNo(order.getOrderNo());
            CashInOption option = cashInOptionDao.findAllById(order.getType());
            if (option != null && gameOrder != null){
                JSONObject json = new JSONObject();
                if (option != null){
                    json.put("type",option.getName());
                    json.put("icon",option.getIcon());
                }
                json.put("id", order.getId());
                json.put("amount", gameOrder.getAmount());
                json.put("orderNo", order.getOrderNo());
                json.put("status", order.getStatus() == 1);
                json.put("addTime", order.getAddTime());
                json.put("updateTime", order.getUpdateTime());
                array.add(json);
            }
        }
//        System.out.printf("array%s\n", array);
        JSONObject json = ResponseData.object("list",array);
        json.put("total",orderPage.getTotalPages());
        return ResponseData.success(json);
    }

    public ResponseData fund(int page, User user, String ip) {
        if (user == null) return ResponseData.error("");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page,12,Sort.by(Sort.Direction.DESC,"id"));
        Page<GameFunds> fundsPage = gameFundsDao.findAllByUserId(user.getId(), pageable);
        JSONArray array = new JSONArray();
        for (GameFunds funds : fundsPage.getContent()){
            JSONObject json = new JSONObject();
            json.put("id", funds.getId());
            json.put("amount", funds.getAmount());
            json.put("addTime", funds.getAddTime());
            json.put("text", funds.getText());
            array.add(json);
        }
//        System.out.printf("array%s\n", array);
        JSONObject json = ResponseData.object("list",array);
        json.put("total",fundsPage.getTotalPages());
        return ResponseData.success(json);
    }

    public ResponseData cashOutGetBalance(User user, String ip) {
        if (user == null) return ResponseData.error("");
        long wBalance = gameOutOrderDao.getAllBywBalance(user.getId());
        if (wBalance < 0) wBalance = - wBalance;
        long freezeBalance = gameOutOrderDao.getAllByFreezeBalance(user.getId());
        if (freezeBalance < 0) freezeBalance = - freezeBalance;
        JSONObject json = ResponseData.object("balance", WaLiUtil.getBalance(user.getId()));
//        json.put("wBalance", new Double(String.format("%.2f", wBalance / 100D)));
//        json.put("freezeBalance", new Double(String.format("%.2f", freezeBalance / 100D)));
        json.put("wBalance", new Double(wBalance));
        json.put("freezeBalance", new Double(freezeBalance));
        return ResponseData.success(json);
    }

    public ResponseData cashOutGetConfig(User user, String ip) {
        if (user == null) return ResponseData.error("");
//        List<GameOutCard> cards = gameOutCardDao.findAllByUserId(user.getId());
//        JSONArray array = new JSONArray();
//        for (GameOutCard card : cards) {
//            JSONObject object = new JSONObject();
//            object.put("id", card.getId());
//            object.put("bank", card.getBank());
//            object.put("card", card.getCard());
//            array.add(object);
//        }
        JSONObject json = ResponseData.object("mini", getOutConfigDouble("mini"));
        json.put("max", getOutConfigDouble("max"));
        json.put("fee", getOutConfigDouble("fee"));
        json.put("rate", getOutConfigDouble("rate"));
//        json.put("cards", array);
        return ResponseData.success(json);
    }
    public JSONObject getCard(GameOutCard card) {
        JSONObject object = new JSONObject();
        object.put("id", card.getId());
        object.put("name", card.getName());
        object.put("bank", card.getBank());
        object.put("card", card.getCard());
        object.put("address", card.getAddress());
        object.put("addTime", card.getAddTime());
        object.put("updateTime", card.getUpdateTime());
        return object;
    }
    public ResponseData cashOutGetCards(int page,User user, String ip) {
        if (user == null) return ResponseData.error("");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page,12,Sort.by(Sort.Direction.DESC,"id"));
        Page<GameOutCard> cardPage = gameOutCardDao.findAllByUserId(user.getId(),pageable);
        JSONArray array = new JSONArray();
        for (GameOutCard card : cardPage.getContent()) {
            array.add(getCard(card));
        }
        JSONObject json = ResponseData.object("list",array);
        json.put("total", cardPage.getTotalPages());
        return ResponseData.success(json);
    }

    public ResponseData cashOutEditCard(long id, String name, String bank, String card, String address, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if (id < 1) return ResponseData.error("");
        GameOutCard outCard = gameOutCardDao.findAllById(id);
        if (outCard == null) return ResponseData.error("");
        if(StringUtils.isNotEmpty(name)) outCard.setName(name);
        if(StringUtils.isNotEmpty(bank)) outCard.setBank(bank);
        if(StringUtils.isNotEmpty(card)) outCard.setCard(card);
        if(StringUtils.isNotEmpty(address)) outCard.setAddress(address);
        outCard.setUpdateIp(ip);
        outCard.setUpdateTime(System.currentTimeMillis());
        gameOutCardDao.saveAndFlush(outCard);
        return ResponseData.success(ResponseData.object("card",getCard(outCard)));
    }

    public ResponseData cashOutAddCard(String name, String bank, String card, String address, User user, String ip) {
        if (user == null) return ResponseData.error("");
        GameOutCard outCard = gameOutCardDao.findAllByUserIdAndCard(user.getId(), card);
        if (outCard != null) return ResponseData.error("卡号已重复添加！");
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(bank) || StringUtils.isEmpty(card)) return ResponseData.error("名字/银行/卡号 必填！");
        outCard = new GameOutCard(user.getId(), name, bank, card, address,ip);
        gameOutCardDao.saveAndFlush(outCard);
        return ResponseData.success(ResponseData.object("card",getCard(outCard)));
    }

    public ResponseData cashOutSetDefault(long id, User user, String ip) {
        return ResponseData.success();
    }

    public ResponseData cashOutRemoveCard(long id, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if (id < 1) return ResponseData.error("");
        GameOutCard outCard = gameOutCardDao.findAllByIdAndUserId(id, user.getId());
        if (outCard == null) return ResponseData.error("收款方式未定义！");
        gameOutCardDao.delete(outCard);
        return ResponseData.success(ResponseData.object("state",true));
    }

    public ResponseData cashOut(long id, long amount, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if (id < 1) return ResponseData.error("未选择收款方式");
        GameOutCard outCard = gameOutCardDao.findAllByIdAndUserId(id, user.getId());
        if (outCard == null) return ResponseData.error("收款方式未定义！");
        double balance = WaLiUtil.getBalance(user.getId());
        if (amount > balance) return ResponseData.error("余额不足！");
        double mini = getOutConfigDouble("mini");
        if (amount < mini) return ResponseData.error("单笔最小提现金额为 "+mini);
        double max = getOutConfigDouble("max");
        if (amount > max) return ResponseData.error("单笔最大提现金额为 "+max);
        double fee = getOutConfigDouble("fee");
        double rate = getOutConfigDouble("rate");
        Double _fee = amount * rate + fee;
        if(_fee > _fee.longValue()){
            _fee = new Double(_fee.longValue() + 1+"");
        }
        _fee = new Double(_fee.longValue()+"");
        Double _amount = amount - _fee;
        GameOutOrder order = new GameOutOrder();
        order.setUserId(user.getId());
        order.setOrderNo(TimeUtil._getTime(0));
        order.setAddTime(System.currentTimeMillis());
        order.setUpdateTime(System.currentTimeMillis());
        order.setStatus(0);
        order.setAmount(amount);
        order.setTotalFee(_amount);
        order.setFee(_fee);
        order.setName(outCard.getName());
        order.setBank(outCard.getBank());
        order.setCard(outCard.getCard());
        order.setAddress(outCard.getAddress());
        order.setIp(ip);
        GameFunds fund = new GameFunds(user.getId(), -(order.getAmount() * 100), "手动提现");
        if(!WaLiUtil.tranfer(user.getId(), -(order.getAmount() * 100))) return ResponseData.error("提现失败，详情联系在线客服！");
        gameOutOrderDao.saveAndFlush(order);
        gameFundsDao.saveAndFlush(fund);
        gameScrollDao.saveAndFlush(new GameScroll(user.getNickname(),order.getAmount() * 100,"手动提现"));
        return ResponseData.success(ResponseData.object("state",true));
    }

    public ResponseData cashOutRecords(int page, User user, String ip) {
        if (user == null) return ResponseData.error("");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page,12,Sort.by(Sort.Direction.DESC,"id"));
        Page<GameOutOrder> orderPage = gameOutOrderDao.findAllByUserId(user.getId(),pageable);
        JSONArray array = new JSONArray();
        for (GameOutOrder order : orderPage.getContent()) {
            JSONObject obj = new JSONObject();
            obj.put("id",order.getId());
            obj.put("amount",order.getAmount());
            obj.put("totalFee",order.getTotalFee());
            obj.put("fee", -order.getFee());
            obj.put("orderNo",order.getOrderNo());
            obj.put("status",order.getStatus());
            obj.put("addTime",order.getAddTime());
            obj.put("updateTime",order.getUpdateTime());
            array.add(obj);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list",array);
        jsonObject.put("total",orderPage.getTotalPages());
        return ResponseData.success(jsonObject);
    }
}
