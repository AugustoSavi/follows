package com.users.follows.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;

@Configuration
public class AwsS3ClientConfig {

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKeyId;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretAccessKey;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.s3.endpoint}")
    private String endpointUrl;

    @Value("${spring.cloud.aws.s3.useLocalProfile}")
    private boolean useLocalProfile;

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder s3ClientBuilder = S3Client.builder()
                .region(Region.of(region))
                .forcePathStyle(true)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                ));

        if (useLocalProfile){
            s3ClientBuilder.endpointOverride(URI.create(endpointUrl));
        }

        return  s3ClientBuilder.build();
    }
}
