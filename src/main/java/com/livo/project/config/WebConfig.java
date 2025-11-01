package com.livo.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /img/uploads/** 요청이 오면 실제 파일 경로로 연결
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:src/main/resources/static/img/");
        registry
                .addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry
                .addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");


        registry.addResourceHandler("/img/common/**")
                .addResourceLocations("classpath:/static/img/common/");


        registry.addResourceHandler("/img/upload/**")
                .addResourceLocations("file:/home/ubuntu/livo-img/upload/");


        registry.addResourceHandler("/img/lecture/**")
                .addResourceLocations("file:/home/ubuntu/livo-img/lecture/");
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/views/", ".jsp");
    }
}
