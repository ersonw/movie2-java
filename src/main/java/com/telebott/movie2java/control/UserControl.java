package com.telebott.movie2java.control;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.data.RequestData;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.pData;
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
@Api(value = "api", tags = "用户接口")
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
    @PostMapping("/register")
    @ApiGlobalModel(component = pData.class, value = "username,password,deviceId,platform,codeId,code")
    public ResponseData register(@RequestBody pData data){
//        if (StringUtils.isEmpty(data.getUsername()) || StringUtils.isEmpty(password)){
//            return ResponseData.error("用户名或密码必填!");
//        }
//        return service.register(username,password, codeId, code, deviceId, platform,ip);
        return ResponseData.fail();
    }
    @GetMapping("/register/sms/{phone}")
    public ResponseData registerSms(@PathVariable("phone") String phone,
                                    @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        System.out.println(phone);
        System.out.println(ip);
        return ResponseData.fail();
    }
}
