package com.telebott.movie2java.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(includeFieldNames = true)
public class ToPayNotify {
    @ApiModelProperty(value= "商户ID",required = false)
    private String mchid;
    @ApiModelProperty(value= "交易金额",required = false)
    private String total_fee;
    @ApiModelProperty(value= "平台单号",required = false)
    private String out_trade_no;
    @ApiModelProperty(value= "交易单号",required = false)
    private String trade_no;
    @ApiModelProperty(value= "加密签名",required = false)
    private String sign;
    @ApiModelProperty(hidden = true)
    private String ip;
}
