package com.swift.fileserver2.service;

import com.swift.fileserver2.FileStorageProperties;
import com.swift.fileserver2.exception.FileStorageException;
import com.swift.fileserver2.exception.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final Path fileStorageLocation;
    private final FileStorageProperties fileStorageProperties;

    public FileStorageService(FileStorageProperties fileStorageProperties) throws IOException {
        this.fileStorageProperties = fileStorageProperties;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        Files.createDirectories(this.fileStorageLocation);
        log.info("File upload directory created: {}", this.fileStorageLocation);
    }

    public String storeFile(MultipartFile file) {
        // Validate file
        validateFile(file);

        // Generate unique filename
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);
        String fileName = generateUniqueFileName(fileExtension);

        try {
            // Copy file to target location
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {} (Original: {})", fileName, originalFileName);
            return fileName;
        } catch (IOException ex) {
            log.error("Failed to store file: {}", originalFileName, ex);
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        if (file.getSize() > fileStorageProperties.getMaxFileSize()) {
            throw new InvalidFileException("File size exceeds maximum limit of 2GB");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName).toLowerCase();

        boolean isValidExtension = Arrays.stream(fileStorageProperties.getAllowedExtensions())
                .anyMatch(ext -> ext.equalsIgnoreCase(fileExtension));

        if (!isValidExtension) {
            throw new InvalidFileException(
                    String.format("Invalid file extension. Allowed: %s",
                            Arrays.toString(fileStorageProperties.getAllowedExtensions()))
            );
        }
    }

    private String generateUniqueFileName(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("file_%s_%s%s", timestamp, uuid, extension);
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex);
    }

    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }
}