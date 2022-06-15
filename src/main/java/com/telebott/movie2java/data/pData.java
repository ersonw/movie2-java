package com.telebott.movie2java.data;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel
public  class pData  {
    public pData(){}

    @ApiModelProperty(name = "username", value= "用户账号或者手机号",required = false)
    private String username;
    @ApiModelProperty(name = "password", value= "用户密码",required = false)
    private String password;
    @ApiModelProperty(name = "deviceId", value= "设备ID",required = false)
    private String deviceId;
    @ApiModelProperty(name = "platform", value= "设备名称",required = false)
    private String platform;

    @ApiModelProperty(name = "phone", value= "手机号",required = false)
    private String phone;
    @ApiModelProperty(name = "codeId", value= "验证码ID",required = false)
    private String codeId;
    @ApiModelProperty(name = "code", value= "验证码",required = false)
    private String code;

    @ApiModelProperty(name = "seek", value= "视频刻度",required = false)
    private long seek;

    @ApiModelProperty(name = "id", value= "唯一ID",required = false)
    private long id;
    @ApiModelProperty(name = "toId", value= "目标ID",required = false)
    private long toId;
    @ApiModelProperty(name = "text", value= "字符串",required = false)
    private String text;

    @ApiModelProperty(hidden = true)
    private String ip;
    @ApiModelProperty(hidden = true)
    private boolean isWeb;
    @ApiModelProperty(hidden = true)
    private String user;
    public User getUser() {
        JSONObject jsonObject = JSONObject.parseObject(user);
        return JSONObject.toJavaObject(jsonObject, User.class);
    }
}
