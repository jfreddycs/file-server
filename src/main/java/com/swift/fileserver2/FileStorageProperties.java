package com.swift.fileserver2;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
@Data
public class FileStorageProperties {
    private String uploadDir = "uploads";
    private long maxFileSize = 2147483648L; // 2GB
    private String[] allowedExtensions = {".zip", ".yml", ".yaml", ".json"};
}
