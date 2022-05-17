package com.telebott.movie2java.service;

import com.telebott.movie2java.dao.AuthDao;
import com.telebott.movie2java.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static AuthInterceptor self;
    @Autowired
    private AuthDao authDao;

    @PostConstruct
    public void init() {
        self = this;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String token = request.getHeader("Token");
//        System.out.println(token);
        System.out.println(request.getRequestURI());
        if (StringUtils.isEmpty(token)){
            response.sendError(105, "Token is required");
//            response.setStatus(105);
            return false;
        }
        User user = self.authDao.findUserByToken(token);
//        System.out.println(user);
        if (user == null){
            response.sendError(106, "User not found");
//            response.setStatus(106);
            return false;
        }
//        System.out.println();
//        request.setAttribute("user", JSONObject.toJSONString(user));
        return true;// 只有返回true才会继续向下执行，返回false取消当前请求
    }
}