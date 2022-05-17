package com.telebott.movie2java.util;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import io.swagger.annotations.ApiModelProperty;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ApiJsonParameterBuilder
 * <p>
 * 将map入参匹配到swagger文档的工具类
 * plugin加载顺序，默认是最后加载
 *
 */
@Component
@Order
public class ApiGlobalModelBuilder implements ParameterBuilderPlugin {

    private static final Logger logger = LoggerFactory.getLogger(ApiGlobalModelBuilder.class);
    @Autowired
    private TypeResolver typeResolver;
    /**
     * 动态生成的Class的包路径 自由定义
     */
    private final static String BASE_PACKAGE = "com.platform.entity.";
    /**
     * 默认类名
     */
    private final static String DEFAULT_CLASS_NAME = "RequestBody";
    /**
     * 序号 防止重名
     */
    private static Integer i = 0;

    @Override
    public void apply(ParameterContext context) {

        try {

            // 从方法或参数上获取指定注解的Optional
            Optional<ApiGlobalModel> optional = context.getOperationContext().findAnnotation(ApiGlobalModel.class);
            if (!optional.isPresent()) {

                optional = context.resolvedMethodParameter().findAnnotation(ApiGlobalModel.class);
            }
            if (optional.isPresent()) {

                String key = DEFAULT_CLASS_NAME + i++;
                ApiGlobalModel apiAnno = optional.get();
                try {

                    //类名重复将导致swagger识别不准确 主动触发异常
                    Class.forName(BASE_PACKAGE + key);
                } catch (ClassNotFoundException e) {

                    String[] fields = apiAnno.value();
                    String separator = apiAnno.separator();
                    ClassPool pool = ClassPool.getDefault();
                    CtClass ctClass = pool.makeClass(BASE_PACKAGE + key);
                    ctClass.setModifiers(Modifier.ABSTRACT);
                    //处理 javassist.NotFoundException
                    pool.insertClassPath(new ClassClassPath(apiAnno.component()));
                    CtClass globalCtClass = pool.getCtClass(apiAnno.component().getName());

                    //从globalCtClass拷贝指定字段到动态创建的类中
                    for (String field : merge(fields, separator)) {

                        //若指定的字段不存在 throw NotFoundException
                        CtField ctField = globalCtClass.getDeclaredField(field);
                        CtField newCtField = new CtField(ctField, ctClass);
                        handleField(newCtField);
                        ctClass.addField(newCtField);
                    }
                    // 将生成的Class添加到SwaggerModels
                    context.getDocumentationContext().getAdditionalModels()
                            .add(typeResolver.resolve(ctClass.toClass()));
                    // 修改Json参数的ModelRef为动态生成的class
                    context.parameterBuilder()
                            .parameterType("body").modelRef(new ModelRef(key)).name("body").description("body");
                }
            }
        } catch (Exception e) {

            logger.error("@ApiGlobalModel Error", e);
        }
    }

    private void handleField(CtField field) {

        //防止private又没有getter
        field.setModifiers(Modifier.PUBLIC);
        //有name的把字段名改为name
        //因为JSON格式化的原因,ApiModelProperty的name属性无效 所以如果有name,直接更改字段名为name
        AnnotationsAttribute annos = ((AnnotationsAttribute) field.getFieldInfo().getAttribute("RuntimeVisibleAnnotations"));
        if (annos != null) {

            Annotation anno = annos.getAnnotation(ApiModelProperty.class.getTypeName());
            if (anno != null) {

                MemberValue name = anno.getMemberValue("name");
                if (name != null) {

                    //这里返回的name会以引号包裹
                    String fName = name.toString().replace("\"", "").trim();
                    if (fName.length() > 0) {

                        field.setName(fName);
                    }
                }
            }
        }

    }

    /**
     * 字符串列表 分隔符 合并
     * A{"a","b,c","d"} => B{"a","d","b","c"}
     *
     * @param arr arr
     * @return list
     */
    private List<String> merge(String[] arr, String separator) {

        List<String> tmp = new ArrayList<>();
        Arrays.stream(arr).forEach(s -> {

            if (s.contains(separator)) {

                tmp.addAll(Arrays.asList(s.split(separator)));
            } else {

                tmp.add(s);
            }
        });
        return tmp;
    }

    @Override
    public boolean supports(DocumentationType delimiter) {

        return true;
    }
}