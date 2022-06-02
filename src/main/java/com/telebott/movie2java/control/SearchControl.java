package com.telebott.movie2java.control;

import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@Api(value = "api", tags = "搜索接口")
public class SearchControl {
    @Autowired
    private SearchService service;

    @GetMapping("/movie/{text}")
    public ResponseData movie(@PathVariable String text,
                              @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                              @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.searchMovie(text,User.getInstance(user), ip);
    }
    @GetMapping("/result/{page}/{id}")
    public ResponseData searchResult(@PathVariable int page,
                              @PathVariable String id,
                              @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                              @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.searchResult(id, page,User.getInstance(user), ip);
    }
    @GetMapping("/movie/cancel/{id}")
    public ResponseData cancel(@PathVariable String id,
                              @RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user ,
                              @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.searchCancel(id, ip);
    }
    @GetMapping("/label/anytime")
    public ResponseData labelAnytime(@RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user,
                                @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.labelAnytime(User.getInstance(user),ip);
    }
    @GetMapping("/label/hot")
    public ResponseData labelHot(@RequestParam(value = "user",required = false) @ApiParam(hidden = true) String user,
                                @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.labelHot(User.getInstance(user),ip);
    }
}
