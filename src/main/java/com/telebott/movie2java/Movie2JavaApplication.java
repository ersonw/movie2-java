package com.telebott.movie2java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class Movie2JavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(Movie2JavaApplication.class, args);
    }

}
