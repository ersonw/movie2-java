package com.telebott.movie2java.control;

import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.service.MyProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
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
}
