package com.users.follows.config;

import com.errorxcode.jxinsta.InstagramException;
import com.errorxcode.jxinsta.JxInsta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class InstagramConfig {

    @Value("${instagram.username}")
    private String username;

    @Value("${instagram.password}")
    private String password;

    @Bean
    public JxInsta jxInstaClient() throws InstagramException, IOException {
        log.info("Creating singleton Instagram client with user: {}", username);
        return new JxInsta(username, password, JxInsta.LoginType.WEB_AUTHENTICATION);
    }
}
