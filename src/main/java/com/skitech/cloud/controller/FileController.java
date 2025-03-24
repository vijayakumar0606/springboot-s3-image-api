/*package com.skitech.cloud.controller;

import com.skitech.cloud.service.GoogleCloudStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final GoogleCloudStorageService storageService;

    public FileController(GoogleCloudStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = storageService.uploadFile(file);
        return ResponseEntity.ok("File uploaded successfully: " + fileName);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<String> getFileUrl(@PathVariable String fileName) {
        URL url = storageService.getFileUrl(fileName);
        return ResponseEntity.ok(url.toString());
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        storageService.deleteFile(fileName);
        return ResponseEntity.ok("File deleted successfully: " + fileName);
    }
}*/
