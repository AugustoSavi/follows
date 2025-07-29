package com.users.follows.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Slf4j
@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @PostConstruct
    public void setup() {
        boolean bucketExists = s3Client.listBuckets()
                .buckets()
                .stream()
                .anyMatch(b -> b.name().equals(bucket));

        if (!bucketExists) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        }
//        try {
//            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
//        } catch (NoSuchBucketException e) {
//            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
//        }
    }

    public String upload(MultipartFile content) {
        if (content.isEmpty()) {
            throw new IllegalArgumentException("File content cannot be empty");
        }
        if (content.getOriginalFilename() == null || content.getOriginalFilename().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        String fileExtension = content.getOriginalFilename()
                .substring(content.getOriginalFilename().lastIndexOf('.'));

        String key = java.util.UUID.randomUUID() + fileExtension;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder().bucket(bucket).key(key).build(),
                    RequestBody.fromInputStream(content.getInputStream(), content.getSize())
            );
            return key;

        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
            return null;
        }
    }


    public byte[] download(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("File key cannot be null or empty");
        }

        return s3Client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()).asByteArray();
    }

}

