package com.telebott.movie2java.control;

import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.pData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.service.CoinService;
import com.telebott.movie2java.util.ApiGlobalModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coin")
//@Api(value = "api", tags = "金币钱包")
public class CoinControl {
    @Autowired
    private CoinService service;
    @GetMapping("/balance")
    public ResponseData balance(@RequestParam(value = "ip") @ApiParam(hidden = true) String ip,
                                @RequestParam(value = "user") @ApiParam(hidden = true) String user){
        return service.balance(User.getUser(user), ip);
    }
    @GetMapping("/buttons")
    public ResponseData buttons(@RequestParam(value = "ip") @ApiParam(hidden = true) String ip,
                                @RequestParam(value = "user") @ApiParam(hidden = true) String user){
        return service.buttons(User.getUser(user), ip);
    }
    @GetMapping("/button/{id}")
    public ResponseData button(@PathVariable("id") long id,
                               @RequestParam(value = "ip") @ApiParam(hidden = true) String ip,
                               @RequestParam(value = "user") @ApiParam(hidden = true) String user){
        return service.button(id,User.getUser(user), ip);
    }
    @PostMapping("/payment")
    @ApiGlobalModel(component = pData.class, value = "toId,id")
    public ResponseData payment(@RequestBody pData data){
        return service.payment(data.getId(), data.getToId(), data.getSchema(), data.getServerName(), data.getServerPort(), data.getUser(),data.getIp());
    }
    @GetMapping("/order/{page}")
    public ResponseData order(@PathVariable int page,
                              @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                              @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.order(page,User.getInstance(user), ip);
    }
    @GetMapping("/fund/{page}")
    public ResponseData fund(@PathVariable int page,
                             @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                             @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.fund(page,User.getInstance(user), ip);
    }
}
