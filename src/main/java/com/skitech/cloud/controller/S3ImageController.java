package com.skitech.cloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.core.sync.ResponseBytes;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class S3ImageController {

    /*private final S3Client s3Client;
    
    private final String bucketName = "skit-bucket"; // Replace with your actual bucket name

    public S3ImageController(S3Client s3Client) {
        this.s3Client = s3Client;
    }*/
    
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3ImageController(@Value("${aws.access-key}") String accessKey,
                     @Value("${aws.secret-key}") String secretKey,
                     @Value("${aws.s3.region}") String region) {
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @GetMapping("/display1/{fileName}")
    public ResponseEntity<byte[]> displayImage(@PathVariable String fileName) {
        try {
            // Fetch the image from S3
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            ResponseBytes<?> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);

            // Determine the Content-Type dynamically
            String contentType = getContentType(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            return new ResponseEntity<>(objectBytes.asByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Helper method to determine Content-Type based on file extension
    private String getContentType(String fileName) {
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".gif")) return "image/gif";
        return "application/octet-stream"; // Default if unknown type
    }
}

