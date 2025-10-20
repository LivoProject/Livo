package com.livo.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /img/uploads/** 요청이 오면 실제 파일 경로로 연결
        registry.addResourceHandler("/img/uploads/**")
                .addResourceLocations("file:src/main/resources/static/img/uploads/");
    }
}
