package com.telebott.movie2java.util;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.util.UriEncoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
public class UrlUtil {
    public static List<String> urls;
    public static void init(List<String> url){
        urls = url;
    }
    public static String encode(String url){
        //            String encodeUrlString = URLEncoder.encode(url, "UTF-8");
        return UriEncoder.encode(url);
    }
    public static String decode(String url){
        try{
            String decodeUrlString = URLEncoder.encode(url, "UTF-8");
            return decodeUrlString;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
