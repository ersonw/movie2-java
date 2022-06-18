package com.telebott.movie2java.control;

import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.pData;
import com.telebott.movie2java.service.ShortVideoService;
import com.telebott.movie2java.util.ApiGlobalModel;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shortVideo")
@Api(value = "api", tags = "短视频接口")
public class ShortVideoControl {
    @Autowired
    private ShortVideoService service;
    @PostMapping("/upload")
    @ApiGlobalModel(component = pData.class, value = "filePath,imagePath,text,duration,files")
    public ResponseData upload(@RequestBody pData data){
        return service.upload(data.getText(), data.getFilePath(),data.getImagePath(), data.getDuration(),data.getFiles(), data.getUser(),data.getIp());
    }
}
