package com.telebott.movie2java.control;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.service.DeviceService;
import com.telebott.movie2java.service.UserService;
import com.telebott.movie2java.util.FromUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device")
//@Api(value = "api", tags = "设备接口")
public class DeviceControl {
    @Autowired
    private DeviceService service;
    @Autowired
    private UserService userService;

    @GetMapping("/check/{deviceId}")
    public ResponseData check(@PathVariable("deviceId") String deviceId,
                              @RequestParam(value = "ip") @ApiParam(hidden = true) String ip,
                              @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String sUser) {
//        System.out.println(deviceId);
        User user = User.getInstance(sUser);
        if (user != null){
            return ResponseData.success(userService.getUserInfo(user));
        }
        return service.checkDevice(deviceId, ip);
    }
}
