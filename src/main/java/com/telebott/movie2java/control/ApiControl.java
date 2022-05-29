package com.telebott.movie2java.control;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.data.GetData;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.pData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.util.ApiGlobalModel;
import com.telebott.movie2java.util.FromUtil;
import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Api(value = "api", tags = "无验证控制器")
@ApiResponses({
        @ApiResponse(code = 200, message = "成功"),
        @ApiResponse(code = 105, message = "未带token请求"),
        @ApiResponse(code = 106, message = "token非法或者登录已过期"),
})
@RestController
@RequestMapping("/api")
public class ApiControl {
    @ApiOperation(value="请求的接口示例", notes = "测试接口")
    @GetMapping("/test/{page}/{limit}")
    public String test1(@PathVariable("page") int page, @PathVariable("limit") int limit) {
        return "ok";
    }


    @GetMapping("/test/{deviceId}")
    public ResponseData test2(@PathVariable("deviceId") String deviceId,@RequestParam(value = "user",required = false) @ApiParam(hidden = true) String sUser) {
        User user = FromUtil.fromUser(sUser);
        System.out.println(user);
        if (user != null){
            return ResponseData.success((JSONObject) (new JSONObject()).put("token", user.getToken()));
        }
        return ResponseData.fail();
    }
//    @Param("")
//    @RequestBody
//    @ModelAttribute
//    @ApiGlobalModel(component = pData.class, value = "limit,page")
//    @PostMapping("/test")
//    public String test(JSONObject data) {
//        return JSONObject.toJSONString(data);
//    }
//    @ApiIgnore

}
