package com.users.follows;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class FollowsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FollowsApplication.class, args);
    }

}
