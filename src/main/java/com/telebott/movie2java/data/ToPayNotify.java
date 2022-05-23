package com.telebott.movie2java.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(includeFieldNames = true)
public class ToPayNotify {
    private String mchid;
    private String total_fee;
    private String out_trade_no;
    private String trade_no;
    private String sign;
}
