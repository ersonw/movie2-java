package com.telebott.movie2java.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(includeFieldNames = true)
@Data
public class OssConfig {
    private static final int TYPE_UPLOAD_OSS_MINIO = 100;
    private static final int TYPE_UPLOAD_OSS_ALIYUN = 101;
    private int type;
    private String bucket;

    private String endPoint;
    private String accessKey;
    private String secretKey;

    private int port;
    private String region;
    private String sessionToken;
    private boolean useSSL;
    private boolean enableTrace;
    public OssConfig() {
        this.type = TYPE_UPLOAD_OSS_MINIO;
        this.useSSL = true;
        this.enableTrace = false;
    }
    public OssConfig getOssConfig(String str) {
        if (str != null)return JSONObject.toJavaObject(JSONObject.parseObject(str),OssConfig.class);
        return null;
    }
}
