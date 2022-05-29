package com.telebott.movie2java.control;

import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.service.UserService;
import com.telebott.movie2java.util.CustomParam;
import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Api(value = "api", tags = "设备接口")
public class UserControl {
    @Autowired
    private UserService service;

    @PostMapping("/login")
    public ResponseData login(@CustomParam("username") String username, @CustomParam("password") String password, @CustomParam("deviceId") String deviceId, @CustomParam("platform") String platform, @CustomParam("ip") String ip){
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return ResponseData.error("用户名或密码必填!");
        }
        return service.login(username,password, deviceId, platform,ip);
    }
    @PostMapping("/register")
    public ResponseData register(@CustomParam("username") String username,
                                 @CustomParam("password") String password,
                                 @CustomParam("codeId") String codeId,
                                 @CustomParam("code") String code,
                                 @CustomParam("deviceId") String deviceId,
                                 @CustomParam("platform") String platform,
                                 @CustomParam("ip") String ip){
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return ResponseData.error("用户名或密码必填!");
        }
        return service.register(username,password, codeId, code, deviceId, platform,ip);
    }
}
