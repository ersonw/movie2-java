package com.telebott.movie2java.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Setter
@Getter
@ToString(includeFieldNames = true)
public class ShortVideoFile {
    private String filePath;
    private String imagePath;
    private String ossConfig;
    public ShortVideoFile(){}
    public ShortVideoFile(String str){
        if (StringUtils.isEmpty(str)) return;
        ShortVideoFile file = JSONObject.toJavaObject(JSONObject.parseObject(str),ShortVideoFile.class);
        this.filePath = file.filePath;
        this.imagePath = file.imagePath;
        this.ossConfig = file.ossConfig;
    }
}
