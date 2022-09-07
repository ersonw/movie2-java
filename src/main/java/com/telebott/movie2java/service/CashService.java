package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.EPayData;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.EPayUtil;
import com.telebott.movie2java.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CashService {
    private static int INDEX_OF_CASH_IN = 0;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private CashInOrderDao cashInOrderDao;
    @Autowired
    private CashInConfigDao cashInConfigDao;
    @Autowired
    private CashInOptionDao cashInOptionDao;
    @Autowired
    private CashOrderDao cashOrderDao;
    @Autowired
    private CashButtonDao cashButtonDao;
    @Autowired
    private CashConfigDao cashConfigDao;
    @Autowired
    private UserBalanceCashDao userBalanceCashDao;
    @Autowired
    private UserConsumeDao userConsumeDao;
    @Autowired
    private AgentService agentService;

    public boolean getConfigBool(String name){
        return getConfigLong(name) > 0;
    }
    public long getConfigLong(String name){
        String value = getConfig(name);
        if(value == null) return 0;
        return Long.parseLong(value);
    }
    public String getConfig(String name){
        List<CashConfig> configs = cashConfigDao.findAllByName(name);
        return configs.isEmpty() ? null : configs.get(0).getVal();
    }
    public ResponseData balance(User user, String ip) {
        if (user == null) return ResponseData.error("");
//        BigDecimal.valueOf(funds.getAmount()).multiply(new BigDecimal(100)).longValue());
        return ResponseData.success(ResponseData.object("balance",userBalanceCashDao.getAllByBalance(user.getId())));
    }
    public ResponseData buttons(User user, String ip) {
        if (user == null) return ResponseData.error("");
        List<CashButton> buttons = cashButtonDao.getAllButtons();
        JSONArray array = new JSONArray();
        for (CashButton b: buttons) {
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
        CashButton button = cashButtonDao.findAllById(id);
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
        CashButton button = cashButtonDao.findAllById(id);
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
        CashOrder order = new CashOrder();
        order.setUserId(user.getId());
        order.setOrderNo(TimeUtil._getTime(0));
        order.setAmount(button.getAmount());
        order.setPrice(button.getPrice() * 100);
        order.setAddTime(System.currentTimeMillis());

        CashInOrder cashInOrder = new CashInOrder();
        cashInOrder.setType(option.getId());
        cashInOrder.setOrderNo(order.getOrderNo());
        cashInOrder.setOrderType(EPayUtil.CASH_ORDER);
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
        cashOrderDao.saveAndFlush(order);
        cashInOrderDao.saveAndFlush(cashInOrder);
//        System.out.printf("%s\n",sb.toString());
        return ResponseData.success(ResponseData.object("url",sb.toString()));
    }
    public boolean handlerOrder(String orderId){
        CashOrder order = cashOrderDao.findAllByOrderNo(orderId);
        CashInOrder inOrder = cashInOrderDao.findAllByOrderNo(orderId);
        if (order == null || inOrder == null) return false;
        User user = userDao.findAllById(order.getUserId());
        if (user == null) return false;
        UserBalanceCash balance = new UserBalanceCash();
        balance.setAddTime(System.currentTimeMillis());
        balance.setAmount(order.getAmount());
        balance.setUserId(user.getId());
        balance.setText("在线充值");
        userBalanceCashDao.save(balance);
//        UserConsume consume = new UserConsume(user.getId(), new Double(inOrder.getTotalFee()).longValue(),"在线充值"+order.getAmount()+"元",1);
//        userConsumeDao.saveAndFlush(consume);
//        agentService.handlerUser(consume);
        return true;
    }
    public ResponseData order(int page, User user, String ip) {
        if (user == null) return ResponseData.error("");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page,12, Sort.by(Sort.Direction.DESC,"id"));
        Page<CashInOrder> orderPage = cashInOrderDao.getAllByCash(user.getId(),pageable);
        JSONArray array = new JSONArray();
        for (CashInOrder order : orderPage.getContent()){
            CashOrder diamondOrder = cashOrderDao.findAllByOrderNo(order.getOrderNo());
            CashInOption option = cashInOptionDao.findAllById(order.getType());
            if (diamondOrder != null &&option != null){
                JSONObject json = new JSONObject();
                json.put("type",option.getName());
                json.put("icon",option.getIcon());
                json.put("id", order.getId());
                json.put("amount", BigDecimal.valueOf(diamondOrder.getAmount()).multiply(new BigDecimal(100)).longValue());
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
        Page<UserBalanceCash> fundsPage = userBalanceCashDao.findAllByUserId(user.getId(), pageable);
        JSONArray array = new JSONArray();
        for (UserBalanceCash funds : fundsPage.getContent()){
            JSONObject json = new JSONObject();
            json.put("id", funds.getId());
            json.put("amount", BigDecimal.valueOf(funds.getAmount()).multiply(new BigDecimal(100)).longValue());
            json.put("addTime", funds.getAddTime());
            json.put("text", funds.getText());
            array.add(json);
        }
//        System.out.printf("array%s\n", array);
        JSONObject json = ResponseData.object("list",array);
        json.put("total",fundsPage.getTotalPages());
        return ResponseData.success(json);
    }
}
