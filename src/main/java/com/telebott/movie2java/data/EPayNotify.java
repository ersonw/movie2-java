package com.telebott.movie2java.data;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.util.JSONUtil;
import com.telebott.movie2java.util.ToolsUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.DigestUtils;

import java.util.Map;

@Setter
@Getter
@ToString(includeFieldNames = true)
public class EPayNotify {
    @ApiModelProperty(value= "商户ID",required = false)
    private Long pid;
    @ApiModelProperty(value= "三方订单号",required = false)
    private String trade_no;
    @ApiModelProperty(value= "商户订单号",required = false)
    private String out_trade_no;
    @ApiModelProperty(value= "支付方式",required = false)
    private String 	type;
    @ApiModelProperty(value= "商品名称",required = false)
    private String name;
    @ApiModelProperty(value= "商品金额",required = false)
    private String money;
    @ApiModelProperty(value= "支付状态",required = false)
    private String trade_status;
    @ApiModelProperty(value= "签名字符串",required = false)
    private String 	sign;
    @ApiModelProperty(value= "签名类型",required = false)
    private String sign_type = "MD5";

    @Override
    public String toString() {
        JSONObject object = getObject();
        object = JSONUtil.getSortJson(object);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry: object.entrySet()) {
            if (entry.getValue() != null&& !entry.getKey().equals("sign")&& !entry.getKey().equals("sign_type")){
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        if (sb.length() > 0){
            return sb.substring(0, sb.length()-1);
        }
        return "";
    }
    public static String getParameter(Object o){
        JSONObject object = JSONObject.parseObject(JSONObject.toJSONString(o));
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry: object.entrySet()) {
            if (entry.getValue() != null && !entry.getKey().equals("ip")){
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        if (sb.length() > 0){
            return sb.substring(0, sb.length()-1);
        }
        return "";
    }
    private JSONObject getObject(){
        JSONObject object = JSONObject.parseObject(JSONObject.toJSONString(this));
        JSONObject json = new JSONObject();
        for (Map.Entry<String, Object> entry: object.entrySet()) {
            if (entry.getValue() != null &&
                    !entry.getKey().equals("sign") &&
                    !entry.getKey().equals("sign_type ") &&
                    !entry.getKey().equals("ip")){
                json.put(entry.getKey(), entry.getValue());
            }
        }
        return json;
    }
    public String getSign(String key) {
        if (this.getSign_type().equalsIgnoreCase("MD5")){
            return DigestUtils.md5DigestAsHex((this.toString() + key).getBytes());
        }
        if(this.getSign_type().equalsIgnoreCase("WXSING")){
            return ToolsUtil.getWxSign(JSONUtil.toStringMap(this.getObject()),key);
        }
        return null;
    }
    public boolean isSign(String key) {
        if(this.getSign() == null) return false;
        return this.getSign(key).equals(this.getSign());
    }
}
