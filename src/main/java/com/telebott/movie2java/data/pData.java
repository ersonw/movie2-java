package com.telebott.movie2java.data;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel
public abstract class pData implements Serializable {
    public pData(){}
    @ApiModelProperty(name = "page",value = "分页",required = false)
    private Integer page;
    @ApiModelProperty(name = "limit",value = "每页显示数量",required = false)
    private Integer limit;
    @ApiModelProperty(hidden = true)
    private String user;
    public User getUser() {
        JSONObject jsonObject = JSONObject.parseObject(user);
        return JSONObject.toJavaObject(jsonObject, User.class);
    }
}
