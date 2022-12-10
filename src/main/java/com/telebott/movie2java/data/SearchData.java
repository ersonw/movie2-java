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
public class SearchData implements Serializable {
    private String id;
    private String text;
    private long userId;
    public SearchData(String text){
        init();
        this.text = text;
    }
    public SearchData(){
        init();
    }
    private void init(){
        UUID uuid = UUID.randomUUID();
        id = uuid.toString().replaceAll("-","");
    }
}
