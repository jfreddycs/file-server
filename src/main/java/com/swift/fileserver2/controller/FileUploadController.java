package com.swift.fileserver2.controller;

import com.swift.fileserver2.dto.FileUploadResponse;
import com.swift.fileserver2.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Upload API", description = "API for uploading files to server")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file", description = "Upload ZIP, YML, YAML, or JSON files up to 2GB")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("Received file upload request: {} (Size: {} bytes)",
                file.getOriginalFilename(), file.getSize());

        String storedFileName = fileStorageService.storeFile(file);

        FileUploadResponse response = FileUploadResponse.builder()
                .fileName(file.getOriginalFilename())
                .storedFileName(storedFileName)
                .fileType(file.getContentType())
                .size(file.getSize())
                .message("File uploaded successfully")
                .status(HttpStatus.OK.value())
                .build();

        log.info("File uploaded successfully: {}", storedFileName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check endpoint")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("File Server is running");
    }
}