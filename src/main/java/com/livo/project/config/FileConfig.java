package com.livo.project.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "file")
@Configuration
@Getter
@Setter
public class FileConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }
}
