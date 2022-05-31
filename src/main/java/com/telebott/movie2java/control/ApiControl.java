package com.telebott.movie2java.control;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.data.*;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.service.ApiService;
import com.telebott.movie2java.util.ApiGlobalModel;
import com.telebott.movie2java.util.FromUtil;
import com.telebott.movie2java.util.ToolsUtil;
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

@Api(value = "api", tags = "根接口")
@ApiResponses({
        @ApiResponse(code = 200, message = "成功"),
        @ApiResponse(code = 105, message = "未带token请求"),
        @ApiResponse(code = 106, message = "token非法或者登录已过期"),
})
@RestController
@RequestMapping("/api")
public class ApiControl {
    @Autowired
    private ApiService service;
    @ApiIgnore
    @PostMapping("/Yzm")
    public ResponseData Yzm(@RequestParam(value = "passwd", required = false) String passwd, @RequestBody YzmData yzmData) {
//        System.out.println(httpServletRequest.getMethod());
//        String jsonStr = ToolsUtil.getJsonBodyString(httpServletRequest);
//        if (jsonStr != null && jsonStr.startsWith("{") && jsonStr.endsWith("}")) {
//            service.handlerYzm(YzmData.getInstance(jsonStr), passwd);
//        }
        return service.handlerYzm(yzmData, passwd);
    }
    @ApiIgnore
    @PostMapping("/toPayNotify")
    public String toPayNotify(@RequestBody ToPayNotify payNotify) {
//        System.out.println(payNotify);
//        if (service.handlerToPayNotify(payNotify)) {
//            return "success";
//        }
//        return "fail";
        return service.handlerToPayNotify(payNotify);
    }
    @ApiIgnore
    @GetMapping("/toPay")
    public String toPay(@ModelAttribute ToPayNotify payNotify) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "</head>\n" +
                "<script type=\"text/javascript\">\n" +
                "\n" +
                "    function run(){\n" +
                "        document.getElementById(\"sp\").click();\n" +
                "    }\n" +
                "</script>\n" +
                "<body οnlοad=\"run()\">\n" +
                "<a href=\"moviescheme://123\">打开应用<h1 id=\"sp\"></h1></a>\n" +
                "</body>\n" +
                "</html>";
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
