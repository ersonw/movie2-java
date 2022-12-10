package com.telebott.movie2java.control;

import com.telebott.movie2java.service.ShortLinkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
@Api(value = "api", tags = "短链接接口")
public class ShortLinkControl {
    @Autowired
    private ShortLinkService service;
    @GetMapping("/s/{id}")
    public void search(@PathVariable String id,
                         @RequestParam(value = "url",required = false) @ApiParam(hidden = true) String url ,
                         @RequestParam(value = "userAgent",required = false) @ApiParam(hidden = true) String userAgent ,
                         @RequestParam(value = "ip") @ApiParam(hidden = true) String ip,
                         HttpServletResponse response){
        service.search(id, url,userAgent, ip,response);
    }
}
