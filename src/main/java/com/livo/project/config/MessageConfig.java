// src/main/java/soo/config/MessageConfig.java
package com.livo.project.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class MessageConfig {
    @Bean
    public LocalValidatorFactoryBean getValidator(MessageSource messageSource) {
        var v = new LocalValidatorFactoryBean();
        v.setValidationMessageSource(messageSource);
        return v;
    }
}
