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
                                     @RequestParam(value = "isWeb",required = false) @ApiParam(hidden = true) boolean isWeb ,
                                     @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.player(id, User.getInstance(user), ip,isWeb);
    }
    @GetMapping("/comment/{page}/{id}")
    public ResponseData comment(@PathVariable long id,
                                @PathVariable int page,
                                     @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                                     @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.comment(id,page, User.getInstance(user), ip);
    }
    @PostMapping("/comment")
    @ApiGlobalModel(component = pData.class, value = "toId,text,seek,id")
    public ResponseData comment(@RequestBody pData data){
        return service.comment(data.getId(), data.getText(),data.getSeek(),data.getToId(), data.getUser(),data.getIp());
    }
    @GetMapping("/comment/delete/{id}")
    @ApiGlobalModel(component = pData.class, value = "toId,text,seek,id")
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
    @GetMapping("/buy/{id}")
    public ResponseData buy(@PathVariable long id,
                             @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                             @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.buy(id, User.getInstance(user),ip);
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
    @GetMapping("/category/tags")
    public ResponseData categoryTags(@RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                             @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.categoryTags(User.getInstance(user), ip);
    }
    @GetMapping("/category/list/{first}/{second}/{last}/{page}")
    public ResponseData categoryList(@PathVariable int first,
                                     @PathVariable long second,
                                     @PathVariable long last,
                                     @PathVariable int page,
                                @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                                @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.categoryList(first,second,last,page, User.getInstance(user), ip);
    }
    @GetMapping("/concentrations")
    public ResponseData concentrations(@RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                                @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.concentrations(User.getInstance(user), ip);
    }
    @GetMapping("/concentrations/anytime/{id}")
    public ResponseData concentrationsAnytime(@PathVariable long id,
                                              @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                                              @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.concentrationsAnytime(id,User.getInstance(user), ip);
    }
    @GetMapping("/concentrations/{id}/{page}")
    public ResponseData concentrations(@PathVariable long id,
                                       @PathVariable int page,
                                              @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                                              @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.concentrations(id,page,User.getInstance(user), ip);
    }
    @GetMapping("/membership/{page}")
    public ResponseData membership(@PathVariable int page,
                                   @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                                   @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.membership(page,User.getInstance(user), ip);
    }
    @GetMapping("/diamond/{page}")
    public ResponseData diamond(@PathVariable int page,
                                   @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                                   @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.diamond(page,User.getInstance(user), ip);
    }
    @GetMapping("/rank/{first}/{second}")
    public ResponseData rank(@PathVariable int first,
                             @PathVariable long second,
                             @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                             @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.rank(first,second,User.getInstance(user), ip);
    }
    @GetMapping("/publicity")
    public ResponseData publicity(@RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                  @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.publicity(User.getInstance(user), ip);
    }
    @GetMapping("/publicity/report/{id}")
    public ResponseData publicityReport(@PathVariable long id,
                                        @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                        @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.publicityReport(id,User.getInstance(user), ip);
    }
}
