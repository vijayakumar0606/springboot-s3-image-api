package com.skitech.cloud.service;
/*
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.api.client.util.Value;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
*/

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.awscore.presigner.SdkPresigner;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(@Value("${aws.access-key}") String accessKey,
                     @Value("${aws.secret-key}") String secretKey,
                     @Value("${aws.s3.region}") String region) {
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
		//this.s3Presigner = null;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        //return "https://" + bucketName + ".s3." + s3Client.region() + ".amazonaws.com/" + fileName;
        return getFileUrl(bucketName, fileName, s3Client);        
    }
    
    public String getFileUrl(String bucketName, String fileName, S3Client s3Client) {
        S3Utilities utilities = s3Client.utilities();
        URL url = utilities.getUrl(GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build());
        return url.toString();
    }  
    
    /**
     * Download a single file from S3 and save it locally
     */
    public Path downloadFile(String fileName) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        ResponseInputStream<?> s3ObjectStream = s3Client.getObject(getObjectRequest);
        
        // Save file locally (temporary path)
        Path tempFile = Files.createTempFile("s3-download-", "-" + fileName);
        Files.copy(s3ObjectStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        return tempFile;
    }
    
    public byte[] displayFile(String fileName) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        ResponseBytes<?> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);

        return objectBytes.asByteArray(); // Return image bytes
    }

    /**
     * List all files in the S3 bucket
     */
    public List<String> listAllFiles() {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
        return listResponse.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }
    
    
    /*
    private final SdkPresigner s3Presigner;
    
    /**
     * Get a file from S3 as an InputStream
     *
    public InputStream downloadFile(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        ResponseInputStream<?> response = s3Client.getObject(getObjectRequest);
        return response;
    }

    /**
     * Get a pre-signed URL for a file from S3 (Valid for 1 hour)
     *
    public String getFileDownloadUrl(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    /**
     * Get pre-signed URLs for multiple files
     *
    public List<String> getMultipleFileUrls(List<String> fileNames) {
        return fileNames.stream()
                .map(this::getFileDownloadUrl)
                .collect(Collectors.toList());
    }
    */
    
}
/*
@Service
//@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    
    @Autowired
    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 =  amazonS3;
    }

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String uploadFile(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
            return amazonS3.getUrl(bucketName, fileName).toString();
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file", e);
        }
    }
}*/


