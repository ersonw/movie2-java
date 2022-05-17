package com.telebott.movie2java.bootstrap;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.AuthDao;
import com.telebott.movie2java.dao.UserDao;
import com.telebott.movie2java.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@WebFilter(filterName = "myFilter", urlPatterns = "/")
@Order(10000)
public class MyFilter implements Filter {
    @Autowired
    private AuthDao authDao;
    @Autowired
    private UserDao userDao;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
//        authDao = new AuthDao();
    }

    private String getJsonBodyString(HttpServletRequest httpServletRequest) {
        try {
            httpServletRequest.setCharacterEncoding("UTF-8");
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(httpServletRequest.getInputStream(), StandardCharsets.UTF_8));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) req;
            String contentType = request.getContentType();
            String token = ((HttpServletRequest) req).getHeader("Token");
            User user = null;
            if (StringUtils.isNotEmpty(token)){
//                user = authDao.findUserByToken(token);
//                if (token.equals("e4188bce3f35436f9dc5f0e627d093e31651674631238")) {
//                    user = userDao.findAllById(1);
//                }
            }
            if (request.getMethod().equals("GET")){
                Map<String, String[]> parameterMap = new HashMap(request.getParameterMap());
                ParameterRequestWrapper wrapper = new ParameterRequestWrapper(request, parameterMap);
                if (user != null){
                    wrapper.addParameter("user", JSONObject.toJSONString(user));
                }
                request = wrapper;
            }else if (request.getMethod().equals("POST")){
                if (contentType != null){
                    if (contentType.equals(MediaType.APPLICATION_JSON_VALUE)){
                        String postContent = getJsonBodyString(request);
                        JSONObject jsStr = null;
                        if (StringUtils.isNotEmpty(postContent) && postContent.startsWith("{") && postContent.endsWith("}")) {
                            //修改、新增、删除参数
                            jsStr = JSONObject.parseObject(postContent);
                        } else {
                            jsStr = new JSONObject();
                        }
                        if (user != null) {
                            jsStr.put("user", JSONObject.toJSONString(user));
                        }
                        postContent = jsStr.toJSONString();
                        //将参数放入重写的方法中
                        request = new BodyRequestWrapper(request, postContent);
                    }else if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                            || contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)){
                        Map<String, String[]> parameterMap = new HashMap(request.getParameterMap());
                        if (user != null) {
                            parameterMap.put("user", new String[]{JSONObject.toJSONString(user)});
                        }
                        request = new ParameterRequestWrapper(request, parameterMap);
                    }
                }
            }
            chain.doFilter(request, response);
        } else {
            chain.doFilter(req, response);
        }
    }

    //获取Request的body数据
    private String getBody(ServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void destroy() {

    }
}
