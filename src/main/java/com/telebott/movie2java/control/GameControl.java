package com.telebott.movie2java.control;

import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.pData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.service.GameService;
import com.telebott.movie2java.util.ApiGlobalModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@Api(value = "api", tags = "游戏接口")
public class GameControl {
    @Autowired
    private GameService service;
    @GetMapping("/scroll")
    public ResponseData scroll(@RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                             @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.scroll(User.getInstance(user), ip);
    }
    @GetMapping("/getBalance")
    public ResponseData getBalance(@RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                             @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.getBalance(User.getInstance(user), ip);
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
    @PostMapping("/enterGame")
    @ApiGlobalModel(component = pData.class, value = "id")
    public ResponseData enterGame(@RequestBody pData data) {
        return service.enterGame(data.getId(), data.getUser(), data.getIp());
    }
    @GetMapping("/test")
    public ResponseData test(@RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                  @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.test(User.getInstance(user), ip);
    }
    @GetMapping("/list")
    public ResponseData list(@RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                  @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.list(User.getInstance(user), ip);
    }
    @GetMapping("/records")
    public ResponseData records(@RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                  @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.records(User.getInstance(user), ip);
    }
    @GetMapping("/buttons")
    public ResponseData buttons(@RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                  @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.buttons(User.getInstance(user), ip);
    }
    @GetMapping("/button/{id}")
    public ResponseData button(@PathVariable long id,
                                        @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                        @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.button(id,User.getInstance(user), ip);
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
    @PostMapping("/payment")
    @ApiGlobalModel(component = pData.class, value = "toId,id")
    public ResponseData payment(@RequestBody pData data) {
        return service.payment(data.getId(),data.getToId(),data.getSchema(),data.getServerName(),data.getServerPort(), data.getUser(), data.getIp());
    }
    @GetMapping("/cashOut/getBalance")
    public ResponseData cashOutGetBalance(@RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.cashOutGetBalance(User.getInstance(user), ip);
    }
    @GetMapping("/cashOut/getConfig")
    public ResponseData cashOutGetConfig(@RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.cashOutGetConfig(User.getInstance(user), ip);
    }
    @GetMapping("/cashOut/getCards/{page}")
    public ResponseData cashOutGetCards(@PathVariable int page,
                                        @RequestParam(value = "user", required = false) @ApiParam(hidden = true) String user,
                                @RequestParam(value = "ip") @ApiParam(hidden = true) String ip) {
        return service.cashOutGetCards(page,User.getInstance(user), ip);
    }
    @PostMapping("/cashOut/editCard")
    @ApiGlobalModel(component = pData.class, value = "name,bank,card,address,id")
    public ResponseData cashOutEditCard(@RequestBody pData data) {
        return service.cashOutEditCard(data.getId(),data.getName(),data.getBank(),data.getCard(),data.getAddress(), data.getUser(), data.getIp());
    }
    @PostMapping("/cashOut/addCard")
    @ApiGlobalModel(component = pData.class, value = "name,bank,card,address")
    public ResponseData cashOutAddCard(@RequestBody pData data) {
        return service.cashOutAddCard(data.getName(),data.getBank(),data.getCard(),data.getAddress(), data.getUser(), data.getIp());
    }
}
