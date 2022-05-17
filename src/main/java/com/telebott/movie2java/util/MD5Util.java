package com.telebott.movie2java.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.DigestUtils;

@Getter
@Setter
public class MD5Util {
    //盐，用于混交md5
    private String salt = "";
    public MD5Util(String salt) {
        this.salt = salt;
    }
    public MD5Util() {
    }
    public String getPassWord(String password){
        String base = password +  salt;
        return getMD5(base);
    }
    public String getMD5(String base) {
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}
