package com.telebott.movie2java.data;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class RequestData {
    @ApiModelProperty(name = "username", value= "用户账号或者手机号",required = false)
    private String username;
    @ApiModelProperty(name = "password", value= "用户密码",required = false)
    private String password;
    @ApiModelProperty(name = "deviceId", value= "设备ID",required = false)
    private String deviceId;
    @ApiModelProperty(name = "platform", value= "设备名称",required = false)
    private String platform;
    @ApiModelProperty(hidden = true)
    private String ip;
    @ApiModelProperty(hidden = true)
    private String user;
    public User getUser() {
        JSONObject jsonObject = JSONObject.parseObject(user);
        return JSONObject.toJavaObject(jsonObject, User.class);
    }
}
