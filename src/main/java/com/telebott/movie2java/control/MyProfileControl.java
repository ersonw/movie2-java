package com.telebott.movie2java.control;

import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.pData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.service.MyProfileService;
import com.telebott.movie2java.util.ApiGlobalModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/myProfile")
@Api(value = "api", tags = "个人中心接口")
public class MyProfileControl {
    @Autowired
    private MyProfileService service;
    @GetMapping("/info")
    public ResponseData info(@RequestParam(value = "ip") @ApiParam(hidden = true) String ip,
                                     @RequestParam(value = "user") @ApiParam(hidden = true) String user){
        return service.info(User.getUser(user), ip);
    }
    @GetMapping("/edit")
    public ResponseData edit(@RequestParam(value = "ip") @ApiParam(hidden = true) String ip,
                                     @RequestParam(value = "user") @ApiParam(hidden = true) String user){
        return service.edit(User.getUser(user), ip);
    }
    @PostMapping("/edit")
    @ApiGlobalModel(component = pData.class, value = "nickname,username,phone,email,text")
    public ResponseData editSave(@RequestBody pData data){
        return service.editSave(data.getNickname(),data.getUsername(),data.getPhone(),data.getEmail(),data.getText(),data.getUser(),data.getIp());
    }
    @GetMapping("/edit/phone/sms/{phone}")
    public ResponseData editPhoneSms(@PathVariable("phone") String phone,
                             @RequestParam(value = "ip") @ApiParam(hidden = true) String ip,
                             @RequestParam(value = "user") @ApiParam(hidden = true) String user){
        return service.editPhoneSms(phone,User.getUser(user), ip);
    }
    @GetMapping("/edit/restPassword/sms")
    public ResponseData restPasswordSms(@RequestParam(value = "ip") @ApiParam(hidden = true) String ip,
                             @RequestParam(value = "user") @ApiParam(hidden = true) String user){
        return service.restPasswordSms(User.getUser(user), ip);
    }
    @PostMapping("/edit/restPassword/verify")
    @ApiGlobalModel(component = pData.class, value = "codeId,code")
    public ResponseData restPasswordVerify(@RequestBody pData data){
        return service.restPasswordVerify(data.getCodeId(),data.getCode(),data.getUser(),data.getIp());
    }
    @PostMapping("/edit/changePassword/verify")
    @ApiGlobalModel(component = pData.class, value = "password")
    public ResponseData changePasswordVerify(@RequestBody pData data){
        return service.changePasswordVerify(data.getPassword(),data.getUser(),data.getIp());
    }
    @PostMapping("/edit/restPassword")
    @ApiGlobalModel(component = pData.class, value = "salt,password")
    public ResponseData restPassword(@RequestBody pData data){
        return service.restPassword(data.getSalt(),data.getPassword(),data.getUser(),data.getIp());
    }
    @PostMapping("/edit/phone")
    @ApiGlobalModel(component = pData.class, value = "codeId,code")
    public ResponseData editPhone(@RequestBody pData data){
        return service.editPhone(data.getCodeId(),data.getCode(),data.getUser(),data.getIp());
    }
    @PostMapping("/edit/avatar")
    @ApiGlobalModel(component = pData.class, value = "imagePath,oss")
    public ResponseData editAvatar(@RequestBody pData data){
//        System.out.printf("%s\n",data.getOss());
        return service.editAvatar(data.getImagePath(),data.getOssConfig(),data.getUser(),data.getIp());
    }
}
