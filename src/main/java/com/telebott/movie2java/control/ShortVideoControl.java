package com.telebott.movie2java.control;

import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.pData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.service.ShortVideoService;
import com.telebott.movie2java.util.ApiGlobalModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shortVideo")
@Api(value = "api", tags = "短视频接口")
public class ShortVideoControl {
    @Autowired
    private ShortVideoService service;

    @PostMapping("/upload")
    @ApiGlobalModel(component = pData.class, value = "filePath,imagePath,text,duration,files")
    public ResponseData upload(@RequestBody pData data) {
        return service.upload(data.getText(), data.getFilePath(), data.getImagePath(), data.getDuration(), data.getFiles(), data.getUser(), data.getIp());
    }
    @PostMapping("/heartbeat")
    @ApiGlobalModel(component = pData.class, value = "id,seek")
    public ResponseData heartbeat(@RequestBody pData data) {
        return service.heartbeat(data.getId(),data.getSeek(), data.getUser(), data.getIp());
    }

    @GetMapping("/concentration/{page}")
    public ResponseData concentration(@PathVariable int page,
                                     @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                     @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.concentration(page, User.getInstance(user), ip);
    }
    @GetMapping("/friend/{id}/{page}")
    public ResponseData friend(@PathVariable long id,
                                     @PathVariable int page,
                                     @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                     @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.friend(id, page, User.getInstance(user), ip);
    }

    @GetMapping("test")
    public ResponseData test() {
        service.test();
        return ResponseData.success("test");
    }
}
