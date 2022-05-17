package com.telebott.movie2java.bootstrap;

import com.alibaba.fastjson.JSONArray;
import com.telebott.movie2java.util.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UrlConfig {
    @Autowired
    ConfigurableApplicationContext run;
    @Autowired
    private WebApplicationContext applicationContext;
    public JSONArray getAllPath(){
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            Map<String, String> map1 = new HashMap<String, String>();
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            PatternsRequestCondition p = info.getPatternsCondition();
            assert p != null;
            for (String url : p.getPatterns()) {
                map1.put("url", url);
            }
            map1.put("className", method.getMethod().getDeclaringClass().getName()); // 类名
            map1.put("method", method.getMethod().getName()); // 方法名
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                map1.put("type", requestMethod.toString());
            }
            list.add(map1);
        }
        return JSONArray.parseArray(list.toString());
    }
    @PostConstruct
    public void init(){
        UrlUtil.init(getAllUrl(run));
//        System.out.println(getAllPath());
    }
    //获取项目所有url
    public List<String> getAllUrl(ConfigurableApplicationContext run) {
        //获取restcontroller注解的类名
        String[] beanNamesForAnnotation = run.getBeanNamesForAnnotation(RestController.class);
        List<String> urls = new ArrayList<>();
        //获取类对象
//        for (String str : beanNamesForAnnotation) {
//            Object bean = run.getBean(str);
//            Class<?> forName = bean.getClass();
//            //获取requestmapping注解的类
//            RequestMapping declaredAnnotation = forName.getAnnotation(RequestMapping.class);
//            StringBuilder url_path = new StringBuilder();
//            if (declaredAnnotation != null) {
//                String[] value = (declaredAnnotation.value());
//                //获取类的url路径
//                url_path = new StringBuilder(value[0]);
//                for (Method method : forName.getDeclaredMethods()) {
//                    GetMapping getMapping = method.getAnnotation(GetMapping.class);
//                    PostMapping postMapping = method.getAnnotation(PostMapping.class);
//                    if (getMapping != null) {
//                        url_path.append(getMapping.value()[0]);
//                        urls.add(url_path.toString());
//                    }else if (postMapping != null){
//                        url_path.append(postMapping.value()[0]);
//                        urls.add(url_path.toString());
//                    }
//                    url_path = new StringBuilder(value[0]);
//                }
//            }
//        }
        return urls;
    }
}
