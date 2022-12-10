package com.telebott.movie2java.data;

import com.telebott.movie2java.util.SmsBaoUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@ToString(includeFieldNames = true)
public class SmsCode implements Serializable {
    private String id;
    private String phone;
    private String code;
    public SmsCode(){
        init();
    }
    public SmsCode(String phone){
        init();
        this.phone = phone;
    }
    private void init(){
        UUID uuid = UUID.randomUUID();
        id = uuid.toString().replaceAll("-","");
        code = SmsBaoUtil.getSmsCode();
    }
}
