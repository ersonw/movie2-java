package com.telebott.movie2java.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(includeFieldNames = true)
public class ResponseData {
    private  int code = 200;
    private  String message;
    private  String data;

    public ResponseData() {}
    public ResponseData(String message) {
        this.message = message;
    }
    public ResponseData(JSONObject data) {
        this.data = JSONObject.toJSONString(data);
    }
    public ResponseData(int code) {
        this.code = code;
    }
    public ResponseData(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public ResponseData(int code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static ResponseData success() {
        return new ResponseData("OK");
    }
    public static ResponseData success(JSONObject data) {
        return new ResponseData(data);
    }

    public static ResponseData fail() {
        return new ResponseData(404);
    }
    public static ResponseData fail(String message) {
        return new ResponseData(404,message);
    }

    public static ResponseData error(String message) {
        return fail(message);
    }
}
