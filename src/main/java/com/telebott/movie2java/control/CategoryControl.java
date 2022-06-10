package com.telebott.movie2java.control;

import com.telebott.movie2java.service.CategoryService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
@Api(value = "api", tags = "分类接口")
public class CategoryControl {
    @Autowired
    private CategoryService service;
}
