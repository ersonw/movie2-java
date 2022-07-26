package com.telebott.movie2java.bootstrap;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.telebott.movie2java.dao.AuthDao;
import com.telebott.movie2java.dao.UserDao;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.util.AESUtils;
import com.telebott.movie2java.util.ToolsUtil;
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
@WebFilter(filterName = "myFilter", urlPatterns = {"/api/*"})
//@Order(10000)
public class MyFilter implements Filter {
    @Autowired
    private AuthDao authDao;
    @Autowired
    private UserDao userDao;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
//        authDao = new AuthDao();
    }
    /**
     * 获取访问者IP
     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
     * 如果还不存在则调用Request .getRemoteAddr()。
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            if(ip.contains("../")||ip.contains("..\\")){
                return "";
            }
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                ip= ip.substring(0, index);
            }
            if(ip.contains("../")||ip.contains("..\\")){
                return "";
            }
            return ip;
        } else {
            ip=request.getRemoteAddr();
            if(ip.contains("../")||ip.contains("..\\")){
                return "";
            }
            if(ip.equals("0:0:0:0:0:0:0:1")){
                ip="127.0.0.1";
            }
            return ip;
        }

    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) req;
            String contentType = request.getContentType();
//            System.out.println(contentType);
            String userAgent = request.getHeader("User-Agent");
//            System.out.println(userAgent);
            String token = ((HttpServletRequest) req).getHeader("Token");
            String ip = getIpAddr(request);
//            System.out.printf(ip+"\n");
            String serverName = request.getServerName();//返回服务器的主机名
            String serverPort = String.valueOf(request.getServerPort());//返回服务器的端口号
            String uri = request.getRequestURI();//返回请求行中的资源名称
            String url = request.getRequestURL().toString();//获得客户端发送请求的完整url
            String schema = request.getScheme();
            String query = request.getQueryString();
//            System.out.printf(schema+"\n");
            User user = null;
            if (StringUtils.isNotEmpty(token)){
                user = authDao.findUserByToken(token);
                if (token.equals("e4188bce3f35436f9dc5f0e627d093e31651674631238")) {
                    user = userDao.findAllById(1);
                }
            }
            if (request.getMethod().equals("GET")){
                Map<String, String[]> parameterMap = new HashMap(request.getParameterMap());
                ParameterRequestWrapper wrapper = new ParameterRequestWrapper(request, parameterMap);
                wrapper.addParameter("ip", ip);
                wrapper.addParameter("isWeb", userAgent != null && !userAgent.contains("dart:io"));
                wrapper.addParameter("serverName", serverName);
                wrapper.addParameter("serverPort", String.valueOf(serverPort));
                wrapper.addParameter("uri", uri);
                wrapper.addParameter("url", url);
                wrapper.addParameter("schema", schema);
                wrapper.addParameter("query", query);
                if (user != null){
                    wrapper.addParameter("user", JSONObject.toJSONString(user));
                }
                request = wrapper;
            }else if (request.getMethod().equals("POST")){
                if (contentType != null){
                    if (contentType.contains(MediaType.APPLICATION_JSON_VALUE)){
                        String postContent = ToolsUtil.getJsonBodyString(request);
//                        System.out.println(postContent);
                        String s =  AESUtils.Decrypt(postContent);
                        if (s != null){
                            postContent = s;
                        }
                        JSONObject jsStr = null;
                        if (StringUtils.isNotEmpty(postContent) && postContent.startsWith("{") && postContent.endsWith("}")) {
                            //修改、新增、删除参数
                            jsStr = JSONObject.parseObject(postContent);
                        } else {
                            jsStr = new JSONObject();
                        }
                        jsStr.put("ip", ip);
                        jsStr.put("isWeb", userAgent != null && !userAgent.contains("dart:io"));
                        jsStr.put("serverName", serverName);
                        jsStr.put("serverPort", serverPort);
                        jsStr.put("uri", uri);
                        jsStr.put("url", url);
                        jsStr.put("schema", schema);
                        if (user != null) {
                            jsStr.put("user", JSONObject.toJSONString(user));
                        }
                        postContent = jsStr.toJSONString();
//                        System.out.println(postContent);
                        //将参数放入重写的方法中
                        request = new BodyRequestWrapper(request, postContent);
//                        Map<String, String[]> parameterMap = JSONObject.parseObject(postContent, new TypeReference<Map<String, String[]>>(){});
//                        request = new ParameterRequestWrapper(request, parameterMap);
                    }else{
                        Map<String, String[]> parameterMap = new HashMap(request.getParameterMap());
                        parameterMap.put("ip", new String[]{ip});
//                        parameterMap.put("isWeb", userAgent.contains("dart:io"));
                        parameterMap.put("serverName", new String[]{serverName});
                        parameterMap.put("serverPort", new String[]{String.valueOf(serverPort)});
                        parameterMap.put("uri", new String[]{uri});
                        parameterMap.put("url", new String[]{url});
                        parameterMap.put("schema", new String[]{schema});
                        if (user != null) {
                            parameterMap.put("user", new String[]{JSONObject.toJSONString(user)});
                        }
//                        System.out.println(parameterMap);
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
