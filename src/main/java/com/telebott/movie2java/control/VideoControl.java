package com.telebott.movie2java.control;

import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.pData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.service.VideoService;
import com.telebott.movie2java.util.ApiGlobalModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/video")
@Api(value = "api", tags = "视频接口")
public class VideoControl {
    @Autowired
    private VideoService service;

    @PostMapping("/heartbeat")
    @ApiGlobalModel(component = pData.class, value = "seek,id")
    public ResponseData heartbeat(@RequestBody pData data){
        return service.heartbeat(data.getId(), data.getSeek(), data.getUser(),data.getIp());
    }
    @GetMapping("/player/{id}")
    public ResponseData player(@PathVariable long id,
                                     @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                                     @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.player(id, User.getInstance(user), ip);
    }

}
