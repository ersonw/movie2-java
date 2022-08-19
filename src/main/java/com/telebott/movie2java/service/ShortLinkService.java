package com.telebott.movie2java.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
public class ShortLinkService {
    public void search(String id, String url, String userAgent, String ip, HttpServletResponse response) {
        try {
//            response.sendRedirect("https://www.baidu.com");
            response.sendError(404);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
