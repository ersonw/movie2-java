package com.telebott.movie2java.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "search_hot")
@Cacheable
@ToString(includeFieldNames = true)
public class SearchHot {
    @Id
    @GeneratedValue
    private long id;
    private String words;
    private String ip;
    private long userId;
    private long addTime;

    public SearchHot(long id, String words, String ip, long userId, long addTime) {
        this.id = id;
        this.words = words;
        this.ip = ip;
        this.userId = userId;
        this.addTime = addTime;
    }
    public SearchHot(){}
    public static JSONObject getObject(SearchHot searchHot){
        JSONObject obj = new JSONObject();
        obj.put("id", searchHot.getId());
        obj.put("words", searchHot.getWords());
        return obj;
    }
    public static JSONArray getObject(List<SearchHot> searchHots){
        JSONArray array = new JSONArray();
        for (SearchHot hot : searchHots) {
            array.add(SearchHot.getObject(hot));
        }
        return array;
    }
}
