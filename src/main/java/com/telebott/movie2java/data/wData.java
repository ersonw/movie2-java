package com.telebott.movie2java.data;
import com.alibaba.fastjson.JSONObject;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class wData {
    private int code;
    private String msg;
    private String data;

    public wRecords getRecords(){
        return JSONObject.parseObject(data,wRecords.class);
    }
    public JSONObject getObject(){
        return JSONObject.parseObject(data);
    }
    public wBalance getBalance(){
        return JSONObject.toJavaObject(getObject(),wBalance.class);
    }
}

