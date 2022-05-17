package com.telebott.movie2java.data;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.entity.User;
import io.swagger.annotations.ApiModelProperty;

public class GetData {
    @ApiModelProperty(value = "page",name = "分页",required = false)
    private Integer page;
    @ApiModelProperty(value = "limit",name = "每页显示数量",required = false)
    private Integer limit;
    @ApiModelProperty(hidden = true)
    private String user;
    public User getUser() {
        JSONObject jsonObject = JSONObject.parseObject(user);
        return JSONObject.toJavaObject(jsonObject, User.class);
    }
}
