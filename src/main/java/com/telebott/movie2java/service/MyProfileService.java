package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.OssConfig;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.SmsCode;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.MD5Util;
import com.telebott.movie2java.util.MobileRegularExp;
import com.telebott.movie2java.util.SmsBaoUtil;
import com.telebott.movie2java.util.ToolsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MyProfileService {
    @Autowired
    private UserConfigDao userConfigDao;
    @Autowired
    private UserService userService;
    @Autowired
    private ShortVideoService shortVideoService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private UserBalanceDiamondDao userBalanceDiamondDao;
    @Autowired
    private UserBalanceCashDao userBalanceCashDao;
    @Autowired
    private UserBalanceCoinDao userBalanceCoinDao;
    @Autowired
    private UserSpreadRecordDao userSpreadRecordDao;
    @Autowired
    private UserSpreadRebateDao userSpreadRebateDao;
    @Autowired
    private SmsRecordDao smsRecordDao;
    @Autowired
    private VideoDao videoDao;
    @Autowired
    private VideoScaleDao videoScaleDao;

    public boolean getConfigBool(String name){
        return getConfigLong(name) > 0;
    }
    public long getConfigLong(String name){
        String value = getConfig(name);
        if(value == null) return 0;
        return Long.parseLong(value);
    }
    public String getConfig(String name){
        List<UserConfig> configs = userConfigDao.findAllByName(name);
        return configs.isEmpty() ? null : configs.get(0).getVal();
    }
    public JSONArray getRecords(long userId){
        List<Video> videos = videoDao.getRecords(userId);
        JSONArray array = new JSONArray();
        for(Video video : videos) {
            JSONObject object = new JSONObject();
            object.put("id",video.getId());
            object.put("title",video.getTitle());
            object.put("picThumb",video.getPicThumb());
            object.put("scale",0);
            VideoScale scale = videoScaleDao.findAllByUserIdAndVideoId(userId, video.getId());
            if (scale != null){
                object.put("scale",scale.getVideoTime());
            }
            object.put("vodDuration",video.getVodDuration());
            array.add(object);
        }
        return array;
    }
    public ResponseData info(User user, String ip) {
        if (user == null) return ResponseData.error("登录已过期!");
        User profile = userDao.findAllById(user.getId());
        profile.setToken(user.getToken());
        authDao.pushUser(profile);
        JSONObject json = new JSONObject();
        json.put("user", userService.getUserInfo(profile));
        json.put("diamond", userBalanceDiamondDao.getAllByBalance(user.getId()));
        json.put("cash", userBalanceCashDao.getAllByBalance(user.getId()));
        json.put("coin", userBalanceCoinDao.getAllByBalance(user.getId()));
        json.put("gain", userSpreadRebateDao.getAllByBalance(user.getId()));
        json.put("carUrl", getConfig("carUrl"));
        json.put("serviceUrl", getConfig("serviceUrl"));
        JSONObject data = new JSONObject();
        data.put("buyDiamond", getConfigBool("buyDiamond"));
        data.put("buyCoin", getConfigBool("buyCoin"));
        data.put("money", getConfigBool("money"));
        data.put("collect", getConfigBool("collect"));
        data.put("download", getConfigBool("download"));
        if(StringUtils.isNotEmpty(json.getString("carUrl"))) data.put("openCar", getConfigBool("openCar"));
        data.put("myVideo", getConfigBool("myVideo"));
        if(StringUtils.isNotEmpty(json.getString("serviceUrl"))) data.put("service", getConfigBool("service"));
        json.put("appData", data);
        json.put("records",getRecords(user.getId()));
        return ResponseData.success(json);
    }
    public JSONObject getEdit(User user) {
        JSONObject json = new JSONObject();
        json.put("nickname", user.getNickname());
        json.put("username", user.getUsername());
        String phone = user.getPhone();
        json.put("phone", phone.substring(0,4) + "****" + phone.substring(phone.length() - 4));
        json.put("text", user.getText());
        json.put("email", user.getEmail());
        json.put("avatar", user.getAvatar());
        return json;
    }
    public ResponseData edit(User user, String ip) {
        if (user == null) return ResponseData.error("");
        User profile = userDao.findAllById(user.getId());
        profile.setToken(user.getToken());
        authDao.pushUser(profile);
        return ResponseData.success(getEdit(profile));
    }

    public ResponseData editSave(String nickname, String username, String phone, String email, String text, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if(StringUtils.isEmpty(nickname) || nickname.length() > 20 || nickname.length() < 3 || ToolsUtil.filterWords(nickname.trim())) return ResponseData.error("昵称长度大于20或者小于3或者包含敏感字符");
        if(StringUtils.isEmpty(username) || username.length() > 20 || username.length() < 6 || ToolsUtil.checkChinese(username.trim())) return ResponseData.error("用户名长度大于20或者小于6或者包含中文字符");
        if(text.length() > 30 || ToolsUtil.filterWords(text)) return ResponseData.error("自我介绍长度超过30或者包含敏感字符");
//        if(StringUtils.isEmpty(phone) || !MobileRegularExp.isMobileNumber(phone)) return ResponseData.error("手机号格式不正确！");
        if(StringUtils.isNotEmpty(email) && !ToolsUtil.checkEmailFormat(email.trim())) return ResponseData.error("邮箱格式不正确！");
        username = username.trim();
        username = username.replaceAll(" ","");
        nickname = nickname.trim();
//        nickname = nickname.replaceAll(" ","");
        User profile = userDao.findByUsername(username);
        if (profile != null && profile.getId() != user.getId()) return ResponseData.error("用户名已存在！");
        profile = userDao.findAllByNickname(nickname.replaceAll(" ",""));
        if (profile != null && profile.getId() != user.getId()) return ResponseData.error("昵称已存在！");

        profile = userDao.findAllById(user.getId());
        profile.setNickname(nickname);
        profile.setUsername(username);
        if(StringUtils.isNotEmpty(email)){
            email = email.trim();
            email = email.replaceAll(" ","");
            User _email = userDao.findAllByEmail(email);
            if (_email != null && _email.getId() != user.getId()) return ResponseData.error("电子邮箱已重复！");
            profile.setEmail(email.trim());
        }
        if (StringUtils.isNotEmpty(text)) profile.setText(text.trim());
        profile.setToken(user.getToken());
        userDao.save(profile);
        authDao.pushUser(profile);
        return ResponseData.success("资料修改成功!",getEdit(profile));
    }

    public ResponseData editAvatar(String imagePath, OssConfig config, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if (StringUtils.isEmpty(imagePath)) return ResponseData.error("");
        if (config == null) return ResponseData.error("");
        String url = shortVideoService.getOssUrl(imagePath, config);
        if (url == null) return ResponseData.error("文件上传失败");
        User  profile = userDao.findAllById(user.getId());
        profile.setAvatar(url);
        userDao.save(profile);
        authDao.pushUser(profile);
        return ResponseData.success("头像上传成功!",getEdit(profile));
    }

    public ResponseData editPhoneSms(String phone, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if (StringUtils.isEmpty(phone)) return ResponseData.error("新手机号不可为空");
        if(!MobileRegularExp.isMobileNumber(phone)) return ResponseData.error("手机号码格式不正确");
        User profile = userDao.findAllByPhone(phone);
        if (profile != null) {
            if(profile.getId() != user.getId()) return ResponseData.error("手机号已绑定其他账号！");
            return ResponseData.error("新手机号与旧手机号一致！");
        }
        return getSendSms(ip, phone);
    }

    public ResponseData editPhone(String codeId, String code, User user, String ip) {
        if (user == null) return ResponseData.error("");
        String phone = userService.verifyCode(codeId,code);
        if (phone == null) return ResponseData.error("验证码不正确或者已失效！");
        if(!MobileRegularExp.isMobileNumber(phone)) return ResponseData.error("手机号码格式不正确");
        User profile = userDao.findAllByPhone(phone);
        if (profile != null) {
            if(profile.getId() != user.getId()) return ResponseData.error("手机号已绑定其他账号！");
            return ResponseData.error("新手机号与旧手机号一致！");
        }
        profile = userDao.findAllById(user.getId());
        profile.setPhone(phone);
        userDao.save(profile);
        authDao.pushUser(profile);
        return ResponseData.success(getEdit(profile));
    }

    public ResponseData restPasswordSms(User user, String ip) {
        User profile = userDao.findAllById(user.getId());
        if(StringUtils.isEmpty(profile.getPhone())) return ResponseData.error("未绑定手机号码！");
        String phone = profile.getPhone();
        return getSendSms(ip, phone);
    }

    private ResponseData getSendSms(String ip, String phone) {
        if (!userService.checkSmsMax(phone)){
            return ResponseData.fail("今日短信发送已达上限！");
        }
        long last = userService.checkSmsLast(phone);
        if (last > 0){
            return ResponseData.fail("操作过于频繁，请在"+last+"秒后重试！");
        }
        SmsCode code= userService._getCode(phone);
        if (code == null){
            code = new SmsCode(phone);
            authDao.removeByPhone(phone);
            authDao.pushCode(code);
        }
        SmsRecord smsRecord = new SmsRecord();
        smsRecord.setIp(ip);
        smsRecord.setCode(code.getCode());
        smsRecord.setPhone(code.getPhone());
        smsRecord.setStatus(0);
        smsRecord.setAddTime(System.currentTimeMillis());
        smsRecordDao.saveAndFlush(smsRecord);
        if (SmsBaoUtil.sendSmsCode(code)){
            smsRecord.setStatus(1);
            smsRecordDao.saveAndFlush(smsRecord);
            return ResponseData.success("短信发送成功！",ResponseData.object("id", code.getId()));
        }
        authDao.popCode(code);
        return ResponseData.fail("短信发送失败，请联系管理员!");
    }

    public ResponseData restPasswordVerify(String codeId, String code, User user, String ip) {
        if (user == null) return ResponseData.error("");
        String phone = userService.verifyCode(codeId,code);
        if (phone == null) return ResponseData.error("验证码不正确或者已失效！");
        User profile = userDao.findAllById(user.getId());
        if (!phone.equals(profile.getPhone())) return ResponseData.error("验证码不正确或者已过期！");
        return ResponseData.success(ResponseData.object("salt", profile.getSalt()));
    }

    public ResponseData restPassword(String salt, String password, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if(StringUtils.isEmpty(password) || StringUtils.isEmpty(salt)) return ResponseData.error("密码未填写");
        User profile = userDao.findAllById(user.getId());
        if (!salt.equals(profile.getSalt())) return ResponseData.error("操作已超时，请返回重试！");
        profile.setSalt(ToolsUtil.getSalt());
        MD5Util md5 = new MD5Util(profile.getSalt());
        profile.setPassword(md5.getPassWord(password));
        userDao.save(profile);
        authDao.pushUser(profile);
        return ResponseData.success(ResponseData.object("state",true));
    }

    public ResponseData changePasswordVerify(String password, User user, String ip) {
//        System.out.println(password);
        if (user == null) return ResponseData.error("");
        if(StringUtils.isEmpty(password)) return ResponseData.error("密码不可为空");
        User profile = userDao.findAllById(user.getId());
        MD5Util md5 = new MD5Util(profile.getSalt());
        if(!md5.getPassWord(password).equals(profile.getPassword())) return ResponseData.error("密码不正确！");
        return ResponseData.success(ResponseData.object("salt", profile.getSalt()));
    }
}
