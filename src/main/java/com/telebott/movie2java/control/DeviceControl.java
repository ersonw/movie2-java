package com.telebott.movie2java.control;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.service.DeviceService;
import com.telebott.movie2java.util.FromUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device")
@Api(value = "api", tags = "无验证控制器")
public class DeviceControl {
    @Autowired
    private DeviceService service;

    @GetMapping("/check/{deviceId}")
    public ResponseData check(@PathVariable("deviceId") String deviceId, @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String sUser) {
        User user = FromUtil.fromUser(sUser);
        System.out.println(user);
        if (user != null){
            return ResponseData.success((JSONObject) (new JSONObject()).put("token", user.getToken()));
        }
        if (service.check(deviceId)){
            return ResponseData.success((JSONObject) (new JSONObject()).put("token",service.getToken(deviceId)));
        }
        return ResponseData.fail();
    }
}
