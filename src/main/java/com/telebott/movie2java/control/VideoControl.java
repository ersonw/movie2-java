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
    @GetMapping("/comment/{page}/{id}")
    public ResponseData comment(@PathVariable long id,
                                @PathVariable int page,
                                     @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                                     @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.comment(id,page, User.getInstance(user), ip);
    }
    @PostMapping("/comment")
    @ApiGlobalModel(component = pData.class, value = "toId,id,text,seek")
    public ResponseData comment(@RequestBody pData data){
        return service.comment(data.getId(), data.getText(),data.getSeek(),data.getToId(), data.getUser(),data.getIp());
    }
    @GetMapping("/comment/delete/{id}")
    @ApiGlobalModel(component = pData.class, value = "toId,id,text,seek")
    public ResponseData commentDelete(@PathVariable long id,
                                @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                                @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.commentDelete(id, User.getInstance(user), ip);
    }
    @GetMapping("/comment/like/{id}")
    @ApiGlobalModel(component = pData.class, value = "toId,id,text,seek")
    public ResponseData commentLike(@PathVariable long id,
                                @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                                @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.commentLike(id, User.getInstance(user), ip);
    }
    @GetMapping("/like/{id}")
    public ResponseData like(@PathVariable long id,
                             @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                             @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.like(id, User.getInstance(user),ip);
    }
    @GetMapping("/share/{id}")
    public ResponseData share(@PathVariable long id,
                             @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                             @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.share(id, User.getInstance(user),ip);
    }
    @GetMapping("/anytime")
    public ResponseData anytime(@RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                                @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.anytime(User.getInstance(user), ip);
    }

}
