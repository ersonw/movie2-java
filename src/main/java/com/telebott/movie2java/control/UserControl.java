package com.telebott.movie2java.control;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.data.RequestData;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.pData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.service.UserService;
import com.telebott.movie2java.util.ApiGlobalModel;
import com.telebott.movie2java.util.CustomParam;
import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
//@Api(value = "api", tags = "用户接口")
public class UserControl {
    @Autowired
    private UserService service;

    @PostMapping("/login")
    @ApiGlobalModel(component = pData.class, value = "username,password,deviceId,platform")
    public ResponseData login(@RequestBody pData data){
        if (StringUtils.isEmpty(data.getUsername()) || StringUtils.isEmpty(data.getPassword())){
            return ResponseData.error("用户名或密码必填!");
        }
        return service.login(data.getUsername(),data.getPassword(), data.getDeviceId(), data.getPlatform(),data.getIp());
    }
    @GetMapping("/logout")
    public ResponseData logout(
            @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                               @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.logout(User.getUser(user),ip);
    }
    @GetMapping("/login/sms/{phone}")
    public ResponseData userLoginSms(@PathVariable("phone") String phone,
                                     @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.userLoginSms(phone, ip);
    }
    @PostMapping("/login/phone")
    @ApiGlobalModel(component = pData.class, value = "codeId,code,deviceId,platform")
    public ResponseData userLoginPhone(@RequestBody pData data){
        if (StringUtils.isEmpty(data.getCodeId())){
            return ResponseData.error("验证码必填!");
        }
        return service.userLoginPhone(data.getCodeId(), data.getCode(), data.getDeviceId(), data.getPlatform(),data.getIp());
//        return ResponseData.fail();
    }
    @PostMapping("/register")
    @ApiGlobalModel(component = pData.class, value = "password,codeId,code")
    public ResponseData register(@RequestBody pData data){
        if (StringUtils.isEmpty(data.getCode()) || StringUtils.isEmpty(data.getCodeId())){
            return ResponseData.error("验证码必填!");
        }
        if (StringUtils.isEmpty(data.getPassword())){
            return ResponseData.error("密码必填!");
        }
        return service.register(data.getPassword(), data.getCodeId(), data.getCode(), data.getIp());
//        return ResponseData.fail();
    }

    @GetMapping("/register/sms/{phone}")
    public ResponseData registerSms(@PathVariable("phone") String phone,
                                    @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.sendSmsRegister(phone, ip);
    }
    @GetMapping("/profile/{id}")
    public ResponseData profile(@PathVariable("id") long id,
                                @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                    @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.profile(id, User.getUser(user),ip);
    }
    @GetMapping("/profile/{id}/video/{page}")
    public ResponseData profileVideo(@PathVariable("id") long id,
                                @PathVariable("page") int page,
                                @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                    @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.profileVideo(id,page, User.getUser(user),ip);
    }
    @GetMapping("/profile/{id}/video/{page}/like")
    public ResponseData profileVideoLike(@PathVariable("id") long id,
                                @PathVariable("page") int page,
                                @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                    @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.profileVideoLike(id,page, User.getUser(user),ip);
    }
    @GetMapping("/my/profile")
    public ResponseData myProfile(@RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                    @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.myProfile(User.getUser(user),ip);
    }
    @GetMapping("/my/profile/video/{page}")
    public ResponseData myProfileVideo(@PathVariable("page") int page,
                                @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                    @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.myProfileVideo(page, User.getUser(user),ip);
    }
    @GetMapping("/my/profile/video/{page}/like")
    public ResponseData myProfileVideoLike(@PathVariable("page") int page,
                                @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                    @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.myProfileVideoLike(page, User.getUser(user),ip);
    }
    @GetMapping("/follow/{id}/{page}")
    public ResponseData follows(@PathVariable("page") int page,
                                       @PathVariable("id") long id,
                                       @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                       @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.follows(id,page, User.getUser(user),ip);
    }
    @GetMapping("/fans/{id}/{page}")
    public ResponseData fans(@PathVariable("page") int page,
                                       @PathVariable("id") long id,
                                       @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                       @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.fans(id,page, User.getUser(user),ip);
    }
    @GetMapping("/follow/{id}/{page}/{text}")
    public ResponseData followsSearch(@PathVariable("page") int page,
                                      @PathVariable("text") String text,
                                       @PathVariable("id") long id,
                                       @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                       @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.followsSearch(id,page,text, User.getUser(user),ip);
    }
    @GetMapping("/fans/{id}/{page}/{text}")
    public ResponseData fansSearch(@PathVariable("page") int page,
                                   @PathVariable("text") String text,
                                       @PathVariable("id") long id,
                                       @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                       @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.fansSearch(id,page,text, User.getUser(user),ip);
    }
    @GetMapping("/share/receive/{text}")
    public ResponseData shareReceive(@PathVariable("text") String text,
                                     @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                       @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.shareReceive(text, User.getUser(user),ip);
    }
    @GetMapping("/share/config")
    public ResponseData shareConfig(@RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                       @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.shareConfig(User.getUser(user),ip);
    }
}
