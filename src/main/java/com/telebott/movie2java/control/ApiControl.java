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
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

//@Api(value = "api", tags = "根接口")
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
    public ResponseData Yzm(@RequestParam(value = "passwd", required = false) String passwd, @RequestBody String yzmData) {
//        System.out.println(httpServletRequest.getMethod());
//        String jsonStr = ToolsUtil.getJsonBodyString(httpServletRequest);
//        if (jsonStr != null && jsonStr.startsWith("{") && jsonStr.endsWith("}")) {
//            service.handlerYzm(YzmData.getInstance(jsonStr), passwd);
//        }
        return service.handlerYzm(YzmData.getInstance(yzmData), passwd);
    }
    @ApiIgnore
    @GetMapping("/info")
    public String info(){
        return service.info();
    }
    @ApiIgnore
    @GetMapping("/ePayNotify")
    public String ePayNotify(@ModelAttribute EPayNotify ePayNotify){
        return service.ePayNotify(ePayNotify);
    }
    @ApiIgnore
    @GetMapping("/ePayReturn")
    public ModelAndView ePayReturn(@ModelAttribute EPayNotify ePayNotify,
                                   @RequestParam(value = "url") @ApiParam(hidden = true) String url,
                                   @RequestParam(value = "query") @ApiParam(hidden = true) String query,
                                   @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
//        System.out.printf("ePayReturn:%s?%s ip:%s\n", url,query, ip);
        return service.ePayReturn(ePayNotify);
    }
    @GetMapping("/payment")
    public ModelAndView payment(@RequestParam(value = "orderId") String orderId,
                                @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
//        System.out.printf("payment:%s ip:%s\n", orderId, ip);
        return service.payment(orderId, ip);
    }
    @GetMapping("/config")
    public ResponseData config(@RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
        return service.config(ip);
    }
    @GetMapping("/payment/{orderId}")
    public ModelAndView paymentOrderId(@PathVariable String orderId,
                                @RequestParam(value = "ip") @ApiParam(hidden = true) String ip){
//        System.out.printf("paymentOrderId:%s ip:%s\n", orderId, ip);
        return service.payment(orderId, ip);
    }
    @PostMapping("/invitation")
    @ApiGlobalModel(component = pData.class, value = "code")
    public ResponseData invitation(@RequestBody pData data){
        return service.invitation(data.getCode(), data.getUser(),data.getIp());
    }
    @PostMapping("/channel")
    @ApiGlobalModel(component = pData.class, value = "code")
    public ResponseData channel(@RequestBody pData data){
        return service.channel(data.getCode(), data.getUser(),data.getIp());
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
    @GetMapping("/test")
    public ResponseData test(){
        return service.test();
    }
}
