package com.swift.fileserver2.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import jakarta.servlet.MultipartConfigElement;

@Configuration
public class FileStorageConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // Set maximum file size to 2GB
        factory.setMaxFileSize(DataSize.ofBytes(2147483648L)); // 2GB
        factory.setMaxRequestSize(DataSize.ofBytes(2147483648L)); // 2GB
        return factory.createMultipartConfig();
    }
}
