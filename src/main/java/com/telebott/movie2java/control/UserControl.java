package com.telebott.movie2java.control;

import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.service.UserService;
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
    public ResponseData login(@Param("username") String username, @Param("password") String password, @Param("deviceId") String deviceId, @Param("platform") String platform){
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return ResponseData.error("用户名或密码必填!");
        }
        return service.login(username,password, deviceId, platform);
    }
}
