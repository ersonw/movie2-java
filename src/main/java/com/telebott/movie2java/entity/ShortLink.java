package com.telebott.movie2java.entity;

import com.telebott.movie2java.util.ToolsUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "short_link")
@Cacheable
@ToString(includeFieldNames = true)
public class ShortLink {
    public ShortLink() {
        this.addTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
        this.id = ToolsUtil.getRandom(6);
        this.status = 1;
    }
    public ShortLink(String link, String url, String ip) {
        this.addTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
        this.id = ToolsUtil.getRandom(6);
        this.link = link;
        this.url = url;
        this.ip = ip;
    }
    @Id
    @GeneratedValue
    private String id;
    private String link;
    private String url;
    private String ip;
    private int status;
    private long addTime;
    private long updateTime;
}
