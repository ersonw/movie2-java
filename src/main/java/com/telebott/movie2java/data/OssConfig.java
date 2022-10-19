package com.telebott.movie2java.data;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(includeFieldNames = true)
@Data
public class OssConfig {
    public static final int TYPE_UPLOAD_OSS_MINIO = 100;
    public static final int TYPE_UPLOAD_OSS_ALIYUN = 101;

    private int type;
    private String bucket;
    @JsonProperty(value = "endPoint")
    private String endPoint;
    @JsonProperty(value = "accessKey")
    private String accessKey;
    @JsonProperty(value = "secretKey")
    private String secretKey;

    private Long port;
    private String region;
    @JsonProperty(value = "sessionToken")
    private String sessionToken;
    @JsonProperty(value = "useSSL")
    private boolean useSSL = false;
    @JsonProperty(value = "enableTrace")
    private boolean enableTrace = false;
    public OssConfig() {
        this.type = TYPE_UPLOAD_OSS_MINIO;
        enableTrace = false;
    }
    public static OssConfig getOssConfig(String str) {
        if (str != null)return JSONObject.toJavaObject(JSONObject.parseObject(str),OssConfig.class);
        return null;
    }

    public boolean getUseSSL() {
        return useSSL;
    }
}
