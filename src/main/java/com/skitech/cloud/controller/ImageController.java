package com.skitech.cloud.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.skitech.cloud.service.S3Service;

/*
import com.skitech.cloud.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final S3Service s3Service;

    public ImageController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = s3Service.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully: "+fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }
}*/

@RestController
@RequestMapping("/api/images")
//@RequiredArgsConstructor
public class ImageController {

    private final S3Service amazonS3Service;

    @Autowired
    public ImageController(S3Service amazonS3Service) {
        this.amazonS3Service = amazonS3Service;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadMultipleFiles(@RequestParam("file") MultipartFile[] files) throws IOException {
    	/*
    	 // Check if no files were uploaded or all files are empty
        if (files == null || files.length == 0 || Arrays.stream(files).allMatch(MultipartFile::isEmpty)) {
            return ResponseEntity.badRequest().body(Collections.singletonList("No files selected for upload."));
        }
    	
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileUrl = amazonS3Service.uploadFile(file);
            fileUrls.add(fileUrl);
        }
        
        if (fileUrls.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonList("All selected files are empty."));
        }
        
        return ResponseEntity.ok(fileUrls);*/
    	
    	//System.out.println("Given Object: "+files);
    	 for (MultipartFile file : files) {
    		 System.out.println("File Name: " + file.getOriginalFilename());
             System.out.println("File Size: " + file.getSize() + " bytes");
             System.out.println("Content Type: " + file.getContentType());	
             System.out.println("File Name: "+file.getName());
             System.out.println("File Resource: "+file.getResource());
    	 }
    	
    	if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body(Collections.singletonList("No files selected for upload."));
        }

        List<String> fileUrls = new ArrayList<>();
        List<String> emptyFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                emptyFiles.add(file.getOriginalFilename() != null ? file.getOriginalFilename() : "Unnamed file");
            } else {
                String fileUrl = amazonS3Service.uploadFile(file);
                fileUrls.add(fileUrl);
            }
        }

        if (!emptyFiles.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonList("Some files are empty and were not uploaded: " + String.join(", ", emptyFiles)));
        }

        return ResponseEntity.ok(fileUrls);
    }
    
    
    /**
     * Download a single file from S3
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String fileName) throws IOException {
        Path filePath = amazonS3Service.downloadFile(fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(filePath));
    }
    
    @GetMapping("/display/{fileName}")
    public ResponseEntity<byte[]> displayImage(@PathVariable String fileName) throws IOException {
        byte[] imageBytes = amazonS3Service.displayFile(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(getContentType(fileName)));

        return ResponseEntity.ok()
                .headers(headers)
                .body(imageBytes);
    }

    // Helper method to determine Content-Type based on file extension
    private String getContentType(String fileName) {
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".gif")) return "image/gif";
        return "application/octet-stream"; // Default if unknown type
    }

    /**
     * List all files in the S3 bucket
     */
    @GetMapping("/files")
    public ResponseEntity<List<String>> listFiles() {
        return ResponseEntity.ok(amazonS3Service.listAllFiles());
    }
}

